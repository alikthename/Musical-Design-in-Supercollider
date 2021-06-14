// =====================================================================
// SuperCollider Workspace
// =====================================================================


b = Buffer.readChannel(s, "non-oneshot-sample-path", channels:[0]);

b = Buffer.readChannel(s, "one-shot-sample-path", channels:[0]);

b.play

Ndef(\granulator).clear;
Ndef(\granulator).ar(2);
Ndef(\granulator).set(\bufnum, b.bufnum);




( // one-shot reverb

Ndef(\granulator, { |bufnum, tFreq=20, overlap=2, rate=1, tFreqMF=0, tFreqMD=0, 
rateMF=0, rateMD=0, posRateMD=0, posRateMF=0, granDur=4|


	var samplePhasor, phasor, gran, env, bufrd; 
	var bufFrames = BufFrames.ir(bufnum); 
	var t;

	var tFreqMod = {
		SinOsc.ar(tFreqMF, Rand(0.0,2pi)) * tFreqMD;
	};

	var rateMod = {
		SinOsc.ar(rateMF, Rand(0.0,2pi)) * rateMD;
	};


	var posRateMod = {
		SinOsc.ar(posRateMF, Rand(0.0,2pi)) * posRateMD;
	};



	var playTrig = Impulse.ar(\playTrFr.kr(0.5));
	
	tFreq = tFreq + tFreqMod.dup;
	rate = rate + rateMod.dup;
	

	t = Impulse.ar(tFreq);
	// t = In.ar(somebus);

	samplePhasor = EnvGen.ar(Env([0, 0, bufFrames], [0, bufFrames / SampleRate.ir], curve: 0), gate: playTrig);
	phasor = EnvGen.ar(Env([0, 0, 1], [0, granDur], curve: 0), gate: playTrig);

	
	bufrd = BufRd.ar(
		numChannels: 1, 
		bufnum: bufnum, 
		phase: samplePhasor, 
		interpolation: 4
	);
	
	bufrd = LeakDC.ar(bufrd);

	gran = GrainBufJ.ar(
		numChannels: 1, 
		trigger: t, 
		dur: tFreq.reciprocal * overlap, 
		sndbuf: bufnum, 
		rate: rate, 
		// pos: phasor + WhiteNoise.ar(posRateMD!2), 
		pos: phasor + posRateMod.dup,
		interp: 2, 
		pan: 0, 
		envbufnum: -1, 
		maxGrains: 512, 
		loop: 0
	) * EnvGen.ar(Env.perc(0.001, granDur * \grainGate.kr(1), curve: \granCurve.kr(-4)), gate: playTrig);


	Mix([
		bufrd * \dry.kr(1.0),
		gran  * \wet.kr(0.5)
	])
	
	// DetectSilence.ar(gran, doneAction: 2); // in case we lauch it without trigger Ugens (with Synth etc)

});


Ndef(\granulator).addSpec(
	\overlap, [0.001, 40, \exp],
	\rate, [-2, 2],
	\posRate, [0, 4],
	\tFreq, \widefreq,
	\tFreqMF, [0.0,80],
	\tFreqMD, [0.0,20],
	\rateMF, [0.0,80],
	\rateMD, [0.0,2],
	\posRateMF, [0.0,80],
	\posRateMD, [0.0,2],
	\dry, [0.0,1],
	\wet, [0.0,1],
	\playTrFr, [0.0,1],
	\granDur, [0.0,8],
	\granCurve, [-10,10],
	\grainGate, [0,1],
).edit;




)




// tFreq modulation + little rate mod
Ndef('granulator').set('wet', 0.20634920634921, 'tFreq', 68.049596318246, 'posRateMF', 0.0, 'posRateMD', 0.0, 'rateMF', 40.21164021164, 'tFreqMF', 22.857142857143, 'tFreqMD', 18.835978835979, 'playTrFr', 0.24867724867725, 'granDur', 6.3915343915344, 'rateMD', 0.031746031746032, 'overlap', 27.015519604492, 'bufnum', b.bufnum, 'dry', 0.8994708994709, 'posRate', 1.0);


// rate modulation only
Ndef('granulator').set('wet', 0.49206349206349, 'tFreq', 68.049596318246, 'posRateMF', 0.0, 'posRateMD', 0.0, 'rateMF', 29.62962962963, 'tFreqMF', 0.0, 'tFreqMD', 0.0, 'playTrFr', 0.24867724867725, 'granDur', 6.8994708994709, 'rateMD', 0.052910052910053, 'overlap', 27.015519604492, 'bufnum', b.bufnum, 'dry', 0.82010582010582, 'posRate', 1.0);


// posRate mod only
Ndef('granulator').set('wet', 0.49, 'tFreq', 82.597877312021, 'posRateMF', 21.164021164021, 'posRateMD', 0.1481481486212, 'rateMF', 0.0, 'tFreqMF', 0.0, 'tFreqMD', 0.0, 'granDur', 1.1428571428571, 'playTrFr', 0.53439153439153, 'rateMD', 0.0, 'overlap', 14.580370622763, 'bufnum', b.bufnum, 'dry', 0.8);

// all parameters at once
Ndef('granulator').set('wet', 0.53968253968254, 'tFreq', 56.063759866166, 'posRateMF', 0.0, 'rateMF', 20.31746031746, 'tFreqMF', 42.328042328042, 'tFreqMD', 7.6190476190476, 'playTrFr', 0.24867724867725, 'granDur', 0.8042328042328, 'rateMD', 0.031746031746032, 'overlap', 7.4400230051039, 'posRate', 1.0, 'bufnum', b.bufnum, 'dry', 0.77777777777778);

// unnatural
Ndef('granulator').set('wet', 0.85714285714286, 'tFreq', 56.063759866166, 'posRateMF', 0.0, 'rateMF', 20.31746031746, 'tFreqMF', 42.328042328042, 'tFreqMD', 7.6190476190476, 'playTrFr', 0.24867724867725, 'granDur', 2.3703703703704, 'rateMD', 0.031746031746032, 'overlap', 21.58814020418, 'posRate', 1.0, 'bufnum', b.bufnum, 'dry', 0.77777777777778);




// on musical phrase sample
Ndef('granulator').set('wet', 0.1957671957672, 'rate', 1.0, 'tFreq', 82.597877312021, 'posRateMF', 0.0, 'posRateMD', 0.0, 'rateMF', 20.31746031746, 'tFreqMF', 0.0, 'tFreqMD', 0.0, 'playTrFr', 0.24867724867725, 'granDur', 5.4603174603175, 'rateMD', 0.0042328039805094, 'overlap', 12.323100555167, 'posRate', 1.0, 'bufnum', b.bufnum, 'dry', 0.84126984126984);


Ndef('granulator').set('granCurve', -0.79365079365079, 'wet', 0.48148148148148, 'rate', 1.0, 'tFreq', 82.597877312021, 'posRateMF', 7.1957671957672, 'posRateMD', 0.010582010582011, 'rateMF', 20.31746031746, 'tFreqMF', 53.333333333333, 'tFreqMD', 10.05291005291, 'playTrFr', 0.24867724867725, 'granDur', 5.4603174603175, 'rateMD', 0.010582010582011, 'overlap', 12.323100555167, 'grainGate', 1.0, 'bufnum', 311, 'dry', 0.84126984126984, 'posRate', 1.0);


( // triggered Env.new
{
    [
		EnvGen.kr(Env(levels: [0, 0.1, 0.2, 0.3], times: [0.1, 0.1, 0.1], curve: 8), gate: Impulse.kr(3)),
		EnvGen.kr(Env(levels: [0, 0.0, 0.2, 0.3], times: [0.0, 0.1, 0.1], curve: 8), gate: Impulse.kr(3))
	]
}.plot(duration: 1);
)














////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////
//////////////////////   GrainBuf as effect   //////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////





( 
Ndef('alik_input_granulate_0').addSpec(
	// \recLevel, [0,1], 
	// \preLevel, [0,1], 
	\feedback, [-2.0,2],
	\inAmp, \inAmp,
	\wet, \bipolar,

	\inputAmp, [0,30],

	\tFreqMF, [0,200],
	\tFreqMD, [0,200],
	\offsetMF, \widefreq,
	\offsetMD, [0.0,1],
	\rateMF, \widefreq,
	\rateMD, [0.0,1],
	\offset, [0.0,1],
	\rate, \bipolar,
	\tFreq, [1,22050, \exp],
	\overlap, [0.01,512.0, \exp],
	\grHpf, \freq,
	\grLpf, \freq,
	\wet, [0.0,1],
	\dry, [0.0,1],
	\outAmp, [0.0,4],
)
)







Ndef('alik_input_granulate_0').clear
Ndef('alik_input_granulate_0').ar(2)
Ndef('alik_input_granulate_0').edit







( 
Ndef('alik_input_granulate_0', {| 
	inAmp=1, outAmp=1, overlap=2, tFreq=20, tFreqMF=0, tFreqMD=0, 
	rate=1, rateMF=0, rateMD=0, 
	offset = 0, offsetMF=0, offsetMD=0
	|


	var input;
	var gDur, readPos, signal, writePos, grained, balance;

	var t, bufFrames, bufRateScale;
	var buf, bufdur;




	var tFreqMod = {
		SinOsc.ar(tFreqMF, Rand(0.0,2pi)) * tFreqMD;
	};

	var rateMod = {
		SinOsc.ar(rateMF, Rand(0.0,2pi)).range(rateMD.neg, rateMD);
		// SinOsc.ar(rateMF, Rand(0.0,2pi)).range(0, rateMD);
	};

	var offsetMod = {
		SinOsc.ar(offsetMF, Rand(0.0,2pi)).range(0, offsetMD);
		// LFDNoise3.ar(offsetMF).range(0,offsetMD(0.0))
	};


	tFreq = tFreq + tFreqMod.dup;
	rate = rate - rateMod.dup;


	bufFrames = 2**16;
	buf = {LocalBuf(bufFrames).clear}!2;
	bufdur = BufDur.ir(buf); 


	writePos = Phasor.ar( end: bufFrames );

	
	t = Impulse.ar(tFreq);


	readPos = 
	writePos 
	- 64 // looks like a controlblocksize ------ why?
	/ bufFrames 
	- offset
	- offsetMod.dup;
	
	// readPos = readPos.clip( 0, bufFrames - 64 / bufFrames );
	
	readPos = Wrap.ar(readPos, 0, 1);




	(
		offset * bufFrames + 64 / SampleRate.ir
	).poll(10, \delaytime);
	



	grained = GrainBufJ.ar(
		numChannels:1,
		loop: 1,
		trigger:t,
		dur: overlap / tFreq,
		sndbuf: buf,
		rate: rate, 
		pos: readPos,
		interp: 4,
		pan: 0,
		envbufnum:-1
	);

	
	grained  = HPF.ar( grained, \grHpf.kr(40) );
	grained  = LPF.ar( grained, \grLpf.kr(15000) );




	input = SoundIn.ar([0,1]) * inAmp;

	// writing granulated + input back to grain buffer
	buf.do { |b i|

		BufWr.ar(grained[i] * \feedback.kr(0.3) + input[i], b, writePos)

		// RecordBuf has an option to overdub, might be of any use for big mess
		// RecordBuf.ar(
		// 	grained[i] * \feedback.kr(0.3) + input[i], 
		// 	b, 
		// 	recLevel: \recLevel.kr(1),
		// 	preLevel: \preLevel.kr(0),
		// );

	}; 
	signal = Mix([
		grained * \wet.kr(1),
		input * \dry.kr(1)
	]) * outAmp;
	


	// signal.tanh
	signal



}).play

)





// good on piano (and cheap)
Ndef('alik_input_granulate_0').set('offset', 0.031746031746032, 'tFreqMD', 0.0, 'wet', 0.4973544973545, 'rateMF', 19.782824080247, 'outAmp', 1, 'rate', 1.0, 'grLpf', 14393.713460023, 'offsetMF', 93.986276007881, 'inAmp', 1, 'tFreqMF', 25.092540737125, 'grHpf', 92.831776672256, 'tFreq', 26.59633942062, 'overlap', 5.304141781977, 'rateMD', 0.0, 'offsetMD', 0.10582010582011, 'dry', 1.0, 'feedback', 0.44444444444445);


// flies around piano
Ndef('alik_input_granulate_0').set('offset', 0.042328042328042, 'tFreqMD', 0.0, 'wet', 0.079365079365079, 'rateMF', 19.782824080247, 'outAmp', 1, 'rate', 1.0, 'grLpf', 8319.1243261437, 'offsetMF', 8.0769777651696, 'inAmp', 1, 'tFreqMF', 0.21943273553761, 'grHpf', 92.831776672256, 'tFreq', 395.23259800231, 'overlap', 102.70706933049, 'rateMD', 0.0, 'offsetMD', 0.5978835978836, 'dry', 1.0, 'feedback', 0.084656084656086);


// drum room
Ndef('alik_input_granulate_0').set('offset', 0.031746031746032, 'wet', 0.15343915343915, 'rate', 1.0, 'tFreq', 58.821600133219, 'grLpf', 11144.529591014, 'inAmp', 1, 'offsetMF', 4245.0550792034, 'offsetMD', 0.037037037037037, 'rateMF', 0.0, 'tFreqMF', 20.10582010582, 'grHpf', 89.385349076664, 'tFreqMD', 0.0, 'outAmp', 1, 'overlap', 9.7726573922977, 'dry', 1.0, 'feedback', 0.13756613756614);


// cheap
Ndef('alik_input_granulate_0').set('offset', 0.031746031746032, 'wet', 0.16402116402116, 'rate', 1.0, 'tFreq', 337.21723538924, 'grLpf', 11144.529591014, 'inAmp', 1, 'offsetMF', 100.25642627688, 'offsetMD', 0.083068783439341, 'rateMF', 0.0, 'tFreqMF', 83.597883597884, 'grHpf', 89.385349076664, 'tFreqMD', 93.121693121693, 'outAmp', 1, 'overlap', 9.7726573922977, 'dry', 1.0, 'feedback', 0.43386243386244);

Ndef('alik_input_granulate_0').set('offset', 0.010582010582011, 'wet', 0.10582010582011, 'rate', 1.0, 'tFreq', 120.0, 'grLpf', 11144.529591014, 'inAmp', 1, 'offsetMF', 43.300320303739, 'offsetMD', 0.22063492100548, 'rateMF', 0.37037037037037, 'tFreqMF', 105.82010582011, 'grHpf', 89.385349076664, 'tFreqMD', 0.0, 'rateMD', 0.0063492061284487, 'outAmp', 1, 'overlap', 15.46489059164, 'dry', 1.0, 'feedback', 0.30687830687831);

Ndef('alik_input_granulate_0').set('offset', 0.026455026455026, 'wet', 0.16402116402116, 'offsetMD', 0.037037037037037, 'rate', 1.0, 'grLpf', 11144.529591014, 'tFreq', 123.38739758059, 'inAmp', 1, 'rateMF', 0.14732846585601, 'tFreqMF', 83.597883597884, 'tFreqMD', 93.121693121693, 'grHpf', 89.385349076664, 'rateMD', 0.48148148148148, 'outAmp', 1, 'overlap', 9.7726573922977, 'offsetMF', 100.25642627688, 'dry', 1.0, 'feedback', 0.43386243386244);

// expensive

Ndef('alik_input_granulate_0').set('outAmp', 1, 'inAmp', 1, 'offset', 0.0052910052910053, 'offsetMF', 8637.9141790185, 'tFreq', 2799.9461420941, 'offsetMD', 0.22751322751323, 'feedback', 0.052910052910052, 'rate', 1.0, 'overlap', 136.83090472716, 'grHpf', 89.385349076664, 'grLpf', 11144.529591014, 'dry', 1.0, 'wet', 0.042328042328042);














(
g = SynthDef('alik_input_granulate_0', {| 
	inAmp=1, outAmp=1, overlap=2, tFreq=20, tFreqMF=0, tFreqMD=0, 
	rate=1, rateMF=0, rateMD=0, 
	offset = 0, offsetMF=0, offsetMD=0
	|


	var input;
	var gDur, readPos, signal, writePos, grained, balance;

	var t, bufFrames, bufRateScale;
	var buf, bufdur;




	var tFreqMod = {
		SinOsc.ar(tFreqMF, Rand(0.0,2pi)) * tFreqMD;
	};

	var rateMod = {
		SinOsc.ar(rateMF, Rand(0.0,2pi)).range(0, rateMD);
	};

	var offsetMod = {
		SinOsc.ar(offsetMF, Rand(0.0,2pi)).range(0, offsetMD);
	};


	tFreq = tFreq + tFreqMod.dup;
	rate = rate - rateMod.dup;


	bufFrames = 2**16;
	buf = {LocalBuf(bufFrames).clear}!2;
	bufdur = BufDur.ir(buf); 


	writePos = Phasor.ar( end: bufFrames );


	t = Impulse.ar(tFreq);


	readPos = 
	writePos 
	- 64 // looks like a controlblocksize ------ why?
	/ bufFrames 
	- offset
	- offsetMod.dup;
	
	// readPos = readPos.clip( 0, bufFrames - 64 / bufFrames );
	
	readPos = Wrap.ar(readPos, 0, 1);

	(
		offset * bufFrames + 64 / SampleRate.ir
	).poll(1, \delaytime);




	grained = GrainBufJ.ar(
		numChannels:1,
		loop: 1,
		trigger:t,
		dur: overlap.lag(5) / tFreq,
		sndbuf: buf,
		rate: rate, 
		pos: readPos,
		interp: 4,
		pan: 0,
		envbufnum:-1
	);


	grained  = HPF.ar( grained, \grHpf.kr(40) );
	grained  = LPF.ar( grained, \grLpf.kr(15000) );




	input = In.ar(\inBus.kr,2) * inAmp;

	// writing granulated + input back to grain buffer
	buf.do { |b i|

		BufWr.ar(grained[i] * \feedback.kr(0.3) + input[i], b, writePos)

	};

	signal = Mix([
		grained * \wet.kr(1).lag(3),
		input * \dry.kr(1)
	]) * outAmp;



	Out.ar(0, 
		signal.tanh
	)



}).add;



SynthDef(\sin_pulsar, { |out=0|
	var a,b,c,d,e,f, f_ ,t,lev, formantA, formantB, overlapA, overlapB;
	lev = \lev.kr(0.3);	
	f = \freq.kr(440);
	f_ = f.clip(50, 1500);

	formantA = \formantA.kr(2);
	formantB = \formantB.kr(6);
	overlapA = \overlapA.ar(2);
	overlapB = \overlapB.kr(2);

	e = EnvGen.kr(
		Env.perc( 
			attackTime: ( f_.linlin( 50, 1500, 0.1, 0.02) ),
			releaseTime: Rand(1,7) 
		), 
		gate: \gate.kr(1)
	);
	
	e = e * PinkNoise.ar(1!2).range( 0.1, 1 ).lag(0.02) ;

	a = GrainSin.ar(
		numChannels: 1, 
		trigger: Impulse.ar(f), 
		// dur:  overlapA * ( 1.05 - e.pow(0.5) )
		dur:  overlapA * ( 0.05 + e.pow(0.5) )
		* SinOsc.ar(e * 2, {Rand(0, 6.28)}.dup).range(0.87,1.15) 
		/ f, 
		freq: f * 
		( formantA  * SinOsc.ar(3 * e, {Rand(0, 6.28)}.dup).range(0.9,1.1) ),
		pan: PinkNoise.ar(0.5!2), 
	);


	b = GrainSin.ar(
		numChannels: 1, 
		trigger: Impulse.ar(f) + Rand(-1,1), 
		// dur:  overlapB * ( 1.05 - e.pow(0.5) ) 
		dur:  overlapB * ( 0.05 + e.pow(0.5) ) 
		*SinOsc.ar(e * 1, {Rand(0, 6.28)}.dup).range(0.87,1.15)  
		/ f,
		freq: f * 
		( formantB * SinOsc.ar(e * 3, {Rand(0, 6.28)}.dup).range(0.9,Rand(1,1.5)) ),
		pan: PinkNoise.ar(0.5!2), 
	);


	c = GrainSin.ar(
		numChannels: 1, 
		trigger: Impulse.ar(f), 
		// dur: Rand(0.5,2)  * ( 0.05 + e.pow(0.5) ) 
		dur: 1.5  * ( 0.05 + e.pow(0.5) ) 
		*SinOsc.ar(e * 1.5, {Rand(0, 6.28)}.dup).range(0.87,1.15)  
		/ f,
		freq: f 
		* SinOsc.ar(e * 2, {Rand(0, 6.28)}.dup).range(0.9,Rand(1,1.5))
		,
		pan: PinkNoise.ar(0.5!2), 
	);

	c = Mix([
		a * ( lev * e ).pow(f_.linlin( 50, 1500, 1.1, 2.5)),
		b * ( lev * 0.8 * e ).pow(f_.linlin( 50, 1500, 0.85, 4.5)),
		c * ( lev * e ).pow(0.7)
	]);


	c = LeakDC.ar(c);

	
	DetectSilence.ar(c, doneAction: 2);
	Out.ar(out, c )
}).add; 

)



~reverbBus = Bus.audio(s, 2);



( 

Routine({
	thisThread.randSeed = 1234;
	// thisThread.randSeed = 2341567;
	// thisThread.randSeed = 4315627;
	// thisThread.randSeed = 1716434;
	// thisThread.randSeed = 2205318;
	Pbind(
		\instrument, \sin_pulsar,
		\dur, Pseq( 2.pow((-4..1).scramble), inf ),
		\legato, Pfunc({rrand(0.01,1)}),
		\degree, Pseq([0,2,5,6,8,11].scramble, inf),
		\octave, Pstutter(3, Pseq([3,4,5], inf) ),
		// \lev, Pstutter(6, Pseq([ 0.2, 1, 1.2 ].scramble, inf)),
		\lev, Pshuf(( 1..10 ) * 0.01, inf),
		\overlapA, Pseq(2.pow([-1,0,1,2]), inf),
		\overlapB, Pseq(2.pow([-1,0,1].scramble), inf),

		\formantA, Pseq([1,2,3], inf),
		// \formantA, 4,
		\formantB, Pseq([3,4,5,6,7].scramble, inf),
		// \formantB, 16,
		\out, ~reverbBus	
		// \outBus, 0
	).play; 




	x = Synth( 'alik_input_granulate_0',
		addAction: \addToTail,
		args: [ 
			\inBus, ~reverbBus,
			'offset', 0.042328042328042, 'tFreqMD', 0.0, 'wet', 0, 'rateMF', 19.782824080247, 'outAmp', 1, 'rate', 1.0, 'grLpf', 8319.1243261437, 'offsetMF', 8.0769777651696, 'inAmp', 1, 'tFreqMF', 0.21943273553761, 'grHpf', 92.831776672256, 'tFreq', 395.23259800231, 'overlap', 102.70706933049, 'rateMD', 0.0, 'offsetMD', 0.5978835978836, 'dry', 1.0, 'feedback', 0.084656084656086
		]);

	8.wait;

	10.do { |i|
		i.postln;
		x.set(	
			'overlap', 5,
			'tFreq', 26.59633942062, 
			'tFreqMD', exprand(1.0,55), 
			'tFreqMF', 25.092540737125, 
			'rate', 1.0, 
			'rateMF', exprand(3,55), 
			'rateMD', rand(0.005), 
			'offset', exprand(0.05,0.7),
			'offsetMF', 93.986276007881, 
			'offsetMD', exprand(0.01,0.4), 
			'grHpf', 92.831776672256, 
			'grLpf', 14393.713460023, 
			'feedback', 0.4,
			'dry', 1 - exprand(0.1,0.6), 
			'wet', 0.7, 
		);

		8.wait;
		
	};
	
	x.set('offset', 0.026455026455026, 'wet', 0.16402116402116, 'offsetMD', 0.037037037037037, 'rate', 1.0, 'grLpf', 11144.529591014, 'tFreq', 123.38739758059, 'inAmp', 1, 'rateMF', 0.14732846585601, 'tFreqMF', 83.597883597884, 'tFreqMD', 93.121693121693, 'grHpf', 89.385349076664, 'rateMD', 0.48148148148148, 'outAmp', 1, 'overlap', 9.7726573922977, 'offsetMF', 100.25642627688, 'dry', 1.0, 'feedback', 0.43386243386244);


}).play
)



