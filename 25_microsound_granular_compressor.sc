Ndef( \compressor).clear;
Ndef( \compressor).ar(2);

(

Ndef( \compressor, {
	var local;
	var attack = \attack.kr(0.01);
	var release = \release.kr(0.1);
	var dry, drywet, in, t, o, r, c, e, lookahead;
	var kneelag;
	var bias;
	
	t = \threshold.kr(-6);
	
	dry = SoundIn.ar([0,1]);
	
	in = dry * \trim.kr(0).dbamp;

	e = in.mean;
	
	e = HPF.ar( e, \hpf.kr(50) );

	e = EnvDetect.ar(e, attack, release); // smooth abs values
	
	// how much we are over by
	o = e.ampdb.excess(t);
	
	// scale the excess value by ratio
	r = \ratio.kr(4);
	c = ( max(o, 0.0) ) * (r.reciprocal - 1);

	kneelag = attack * \knee.kr(0.0);

	c = c.lag( kneelag );
	c = c.dbamp;
	
	lookahead = \lookahead.kr(0);
	in = DelayC.ar( in, 0.5, lookahead.lag(0.4) ); // lookahead
	in = in * c;
	in = in	* \gain.kr(0).dbamp;
	bias = K2A.ar(\bias.kr(0.0));

	in = Select.ar( \saturate.kr(1), [in, (in + bias).softclip - bias] );
	in = LeakDC.ar(in);

	drywet = \dry_wet.kr(1);
	Mix([
		in * drywet,
		DelayC.ar( dry * (1 - drywet), 0.5, lookahead.lag(0.4) )
	])
	

} ).play;


( 
Spec.add(\attack, [0.0000001,0.1, \exp]);
Spec.add(\release, [0.0000001,0.4, \exp]);
Spec.add(\threshold, [0,-120]);
Spec.add(\trim, [0,60]);
Spec.add(\gain, [-40,40]);
Spec.add(\ratio, [1,20, \exp]);
Spec.add(\dry_wet, [0,1]);
Spec.add(\hpf, [10, 1000]);
Spec.add(\knee, [0.0, 10]);
Spec.add(\lookahead, [0.0,1]);
Spec.add(\saturate, \switch);
Spec.add(\bias, [0.0, 1.0]);
);


Ndef('compressor').set('threshold', -58.604651162791, 'saturate', 0.0, 'lookahead', 0.037037037037037, 'gain', 34.448135843484, 'release', 0.053953680737862, 'bias', 0.0, 'dry_wet', 1.0, 'knee', 1.0077519379845);



Ndef( \compressor).edit;

)







//// Analysis plots --begin

({
EnvDetect.ar(SinOsc.ar, 0.01,0.01);
}.plot(1))

({
EnvDetect.ar(SinOsc.ar, 0.01,0.01).ampdb;
}.plot(1))

({
max( EnvDetect.ar(SinOsc.ar, 0.01,0.01).ampdb.excess(-10), 0);

}.plot(1))

({
var r = 2;
max( EnvDetect.ar(SinOsc.ar, 0.01,0.01).ampdb.excess(-10), 0) * (r.reciprocal - 1);
}.plot(1))

({
var r = 10;
var db = max( EnvDetect.ar(SinOsc.ar, 0.01,0.01).ampdb.excess(-10), 0) * (r.reciprocal - 1);
db.dbamp
}.plot(1))

///// Analysis plots --end





Ndef(\analysis).clear;
Ndef(\analysis).ar(1);



(
Ndef(\analysis, { 
	
	var input, in, e, thresh, c, attack, release, kneelag;
	input = SoundIn.ar([0,1]);

	
	attack = \attack.kr(0.01);
	release = \release.kr(0.1);
	thresh = \threshold.kr(-6);
	in = input * \trim.kr(0).dbamp;

	e = in.mean;
	
	e = HPF.ar( e, \hpf.kr(50) );

	e = EnvDetect.ar(e, attack, release);
	
	// how much we are over by
	o = e.ampdb.excess(thresh);
	
	// scale the excess value by ratio
	r = \ratio.kr(4);
	c = ( max(o, 0.0) ) * (r.reciprocal - 1);

	kneelag = attack * \knee.kr(0.0);

	c = c.lag( kneelag );
	c = c.dbamp;
	

});

Ndef('analysis').set('threshold', -58.6, 'release', 0.05, 'knee', 1.01);

Ndef(\analysis).edit;
)


Ndef( \comp).clear;
Ndef( \comp).ar(2);


(

Ndef( \comp, {
	var dry, in, c, lookahead;
	var bias;
	var gain = \gain.kr(0).dbamp;
	dry = SoundIn.ar([0,1]);
	
	in = dry;

	c = Ndef(\analysis).ar;
	
	lookahead = \lookahead.kr(0);
	in = DelayC.ar( in, 0.5, lookahead); // lookahead
	in = in * c;
	in = in	* gain;
	bias = K2A.ar(\bias.kr(0.0));

	in = Select.ar( \saturate.kr(1), [in, (in + bias).softclip - bias] );
	in = LeakDC.ar(in);

	Mix([
		in * \wet.kr(1),
		DelayC.ar( dry * gain * \dry.kr(1), 0.5, lookahead )
	])
	

} ).play;

Ndef('comp').set('gain', 33.0, 'lookahead', 0.015503875968992, 'dry', 0.0);

Ndef( \comp).edit;


)













Ndef(\granular_comp).clear
Ndef(\granular_comp).ar(2)




( 
Ndef('granular_comp').addSpec(
	\lookahead, [0.0,1],

	\saturate, \switch,
	\lpf, [0.0, 0.2],
	\inAmp, \inAmp,
	\wet, \bipolar,
	\inputAmp, [0,30],
	\lookahead, [0.0,4100],
	\rate, [0,1],
	\tFreq, [1,22050, \exp],
	\overlap, [0.01,512.0, \exp],
	\hpf, \freq,
	\wet, [0.0,1],
	\dry, [0.0,1],
	\gain, [0.0,40],
)
)



( 
( 
Ndef('granular_comp', {| 
	inAmp=1, gain=0, lookahead = 0, tFreq=20, overlap=2, rate=1, lpf=0 
	|


	var input;
	var readPos, signal, writePos, grained, balance;

	var t, bufFrames;
	var buf, bufdur;

	var sc;



	bufFrames = 2**16;
	buf = {LocalBuf(bufFrames).clear}!2;
	bufdur = BufDur.ir(buf); 


	writePos = Phasor.ar(
		0, 
		rate,
		0, 
		bufFrames 
	);


	input = SoundIn.ar([0,1]) * inAmp;
	// writing input to grain buffer
	buf.do { |b i| BufWr.ar( input[i], b, writePos)
	};

	
	t = Impulse.ar(tFreq);


	readPos = Wrap.ar(
		writePos - 64 - lookahead / bufFrames, 
		0, 
		1
	);

	
	sc = Ndef(\analysis).ar;

	grained = GrainBufJ.ar(
		numChannels:1,
		loop: 1,
		trigger:t,
		// dur: overlap / tFreq,
		dur: sc * overlap / tFreq,
		sndbuf: buf,
		rate: rate - lpf, 
		pos:readPos,
		interp: 4,
		// grainAmp: sc,
		pan: 0,
		envbufnum:-1
	);

	
	grained  = HPF.ar( grained, \hpf.kr(40) );

	
	signal = Mix([
		LeakDC.ar( grained ) * \wet.kr(1),
		DelayC.ar( 
			input, 
			0.5, 
			(64 + lookahead / SampleRate.ir )
		) * \dry.kr(0)
	]) * gain.dbamp;
	


	signal.tanh



}).play;





Ndef('granular_comp').edit

)

)











( // using overlap

Ndef('analysis').set('threshold', -58.6, 'ratio', 4.0, 'release', 0.095152954811494, 'knee', 1.01);

Ndef('granular_comp').set('lookahead', 2288.3720930233, 'wet', 0.96124031007752, 'rate', 1.0, 'tFreq', 5054.5412673637, 'lpf', 0.024338624338624, 'inAmp', 0.7751938, 'gain', 1.0542635658915, 'overlap', 337.20506220588, 'dry', 0.0);
)




( // lofi
Ndef('granular_comp').set('lookahead', 127.13178294574, 'wet', 0.66666666666667, 'rate', 0.2, 'tFreq', 4328.5464121672, 0.7751938, 'overlap', 157.82885262499, 'gain', 1.3643410852713, 'dry', 0.0);
Ndef('analysis').set('threshold', -58.6, 'ratio', 1.7870536713698, 'release', 0.068293378346003, 'attack', 0.0055486335881451, 'knee', 1.01);
)




