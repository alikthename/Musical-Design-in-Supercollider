

( 
// s.waitForBoot(
{ 
	var n, e, d, onepole, response, irbuffers, nchannels = 2;




	n = 2 * 44100; 

	//// white noise
	// d = nchannels.collect{ n.collect{ |j| var p = j/n; [ 0, rrand(-0.5,0.5)].wchoose([ 1 - p, p ])} };
	//// ~gaussian~ noise
	d = nchannels.collect{ n.collect{ |j| var p = j/n; [ 0, sum3rand(0.5)].wchoose([ 1 - p, p ])} };
	///// velvet noise
	// d = nchannels.collect{ n.collect{ |j| var p = j/n; [ 0, [-0.5,0.5].choose].wchoose([ 1 - p, p ])} };

	// filtering
	// out(i) = ((1 - abs(coef)) * in(i)) + (coef * out(i-1)) SC OnePole

	// onepole = {arg input, coef=0.5;
	// 	var outPrev = input[0];
	// 	(input.size-1).collect({|i| 
	// 		outPrev = ((1 - coef) * input[i+1]) + (coef * outPrev);
	// 		outPrev;
	// 	})
	// };


	/// coef gets bigger to the end of inpulse response (darkening)

	onepole = {arg input, startcoef=0.5, absorpCurve = 0.4;
		var coef = startcoef, coef_;
		var outPrev = input[0];
		(input.size-1).collect({|i| 
			coef = coef + (input.size.reciprocal * (1 - startcoef ));
			// coef.postln;
			coef_ = coef.pow(absorpCurve);
			outPrev = ((1 - coef_) * input[i+1]) + (coef_ * outPrev);
			outPrev;
		})
	};


	d = d.collect({|it i| onepole.value(it, 0.7, 0.8) });


	e = Env([ 1, 1, 0 ], [ 0.1,1.9 ], -6).discretize(n);

	response = d.collect({|it| it * e });
	
	// ~r = response;

	irbuffers = nchannels.collect { |i|
		Buffer.loadCollection(s, response[i])
	};

	s.sync;

	~fftsize=1024; // also 4096 works on my machine; 1024 too often and amortisation too pushed, 8192 more high load FFT

	~irspectra = nchannels.collect({ |i| Buffer.alloc(s, PartConv.calcBufSize(~fftsize, irbuffers[i]), 1)});
	s.sync;

	nchannels.do { |i| ~irspectra[i].preparePartConv( irbuffers[i], ~fftsize ) };

	"done".postln;

	irbuffers.do { |it| it.free};

}.fork
)




~b = Buffer.read(s, "/mnt/4/oth/study/zvukovoy_obraz_prostranstva/dissertation/experiment 2/denon-ali-mono.wav")

(
Ndef( \conv).addSpec(
	\dry, [0.0,2], 
	\er, [0.0,2], 
	\tail, [0.0,2], 
	\lpfRefl, [0.0,0.99999],
	\hpfRefl, \freq

);
)
( // imp response (we have extra one at the begining bfore actual imp
// response (~fftsize/2 samples))

Ndef( \conv,

	{ var input, kernel, conv, er;
		var dcompen = ~fftsize / 2 - s.options.blockSize / 44100;
		// input= Impulse.ar(0.5);
		input= PlayBuf.ar(1, ~b.bufnum, loop:1)!2;
		er = Reflector.ar(
			input* 0.5,
			numReflcs: 6, 
			delayOffset: 0.02, 
			scaleDelays: 1, 
			spread: 1, 
			reflPan: Rand(-1,1),
			lpfRefl: \lpfRefl.kr(0.7), 
			hpfRefl: \hpfRefl.kr(40),
		);

		conv = PartConv.ar(input, ~fftsize, ~irspectra.collect({|it| it.bufnum }));
		conv = conv * 0.8 ;
		
		Mix([
			DelayN.ar( input, dcompen, dcompen )  * \dry.kr(1),
			DelayN.ar( er, dcompen, dcompen )  * \er.kr(1),
			DelayN.ar( conv, 0.05, 0.05 ) * \tail.kr(0.5)
		]) *0.3
	}
).play;

Ndef('conv').set('er', 0.76190476190476, 'lpfRefl', 0.66666, 'hpfRefl', 57.722808828602, 'tail', 0.21164021164021, 'dry', 1.026455026455);

// Ndef('conv').set('er', 0.63492063492063, 'lpfRefl', 0.66666, 'hpfRefl', 57.722808828602, 'tail', 0.68783068783069, 'dry', 1.1005291005291);
)




(
SynthDef(\foo, { 
	var a,b,c,e;
	var freq, mFreq, mInd;
	var dir, er, dry;
	var dcompen = ~fftsize / 2 - BlockSize.ir / SampleRate.ir;

	freq = EnvGen.kr(Env.perc(Rand(0.001,0.004), Rand(0.001, 0.02 ))) *
	ExpRand(50,1000) + ExpRand(150,1000);

	// mFreq = freq 
	// *  2.pow( Rand(-5, 3) ) 
	// * EnvGen.kr(Env.perc(Rand(0.01,0.5), Rand(0.01,1.5))) 
	// + ( freq * (IRand(1,5)/IRand(1/5)) ) ;

	mFreq = 
	freq 
	*  2.pow( Rand(-1, 3) ) 
	* EnvGen.kr(Env.perc(ExpRand(0.001,0.005), Rand(0.001,0.005))) 
	+ 
	( freq * (IRand(1,5)/IRand(1,5)) ) ;

	mInd = freq 
	* 2.pow( Rand(-5, 1) ) 
	* EnvGen.kr(Env.perc(ExpRand(0.01,0.5), ExpRand(0.01,1.5))) 
	+ (freq * 2.pow( Rand(-5, 3) )) 
	;

	a = SinOsc.ar( freq + SinOsc.ar( mFreq, mul: mInd ) + Dust2.ar(500, freq * 0.5  ).lag(0.01));

	a = a * EnvGen.kr(Env.perc(
		ExpRand( 0.001,0.005 ), 
		TChoose.kr( 1, [5, ExpRand(0.01,0.05)] )
	)) * 0.3;

	a = LeakDC.ar(LPF.ar(a, 10000));
	
	dir = Pan2.ar( a * Rand(0.4, 0.8), Rand(-1,1) );
	
	er = Reflector.ar(
		input: a * 0.1, 
		numReflcs: 6, 
		delayOffset: 0.02, 
		scaleDelays: 1, 
		spread: 1, 
		reflPan: Rand(-1,1),
		lpfRefl: 0.9, 
		hpfRefl: 50
	);
	
	dry = Rand(0.1, 0.8);

	Out.ar( 0, DelayN.ar(
		OnePole.ar( 
			dir 
			+ 
			er
			, 
			1 - dry 
		) * dry, 
		// dir + er * dry,
		dcompen,
		dcompen 
	));

	// // Out.ar( \effectBus.kr, 1 - dry * dir);
	Out.ar( \effectBus.kr, 0.5 * dir);
	// Out.ar( \effectBus.kr, 0.5 * er);

	DetectSilence.ar(a, doneAction: 2);

}).add;
)

~reverbBus = Bus.audio(s,2);

( 
Ndef( \converb,

	{ var input, kernel, conv, er;
		var dcompen = ~fftsize / 2 - s.options.blockSize / 44100;
		// input= Impulse.ar(0.5);
		input= In.ar(~reverbBus.index,2);
		conv = PartConv.ar(OnePole.ar(input, 1 - \inBwth.kr(0.9)), ~fftsize, ~irspectra.collect({|it| it.bufnum }));
		
		DelayN.ar( conv * 0.6, 0.05, 0.05 ) 
	}
).play;

Ndef('converb').play(vol: 0.62326869806094);
)


( 
Routine {
	loop{
		Synth(\foo, [\effectBus, ~reverbBus.index]);
		rrand(0.01,1).wait;
		// 2.pow(rrand(-3,-2)).wait;
	}
}.play; 
)






















