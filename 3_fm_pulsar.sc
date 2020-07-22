( 
Spec.add(\overlap, [0.0,12]);
Spec.add(\cf, \widefreq);
Spec.add(\mf, [0,8,\lin,0,1]);
Spec.add(\freq, \widefreq);
Spec.add(\mInd, [0.0,10]);
)

Ndef(\fm_pulsar).clear;





( // basic
Ndef(\fm_pulsar, { 
	var a,b,c,d,e,f;
	var cf, mf, mInd;
	var overlap;

	overlap	= \overlap.kr(3);

	f = \freq.kr(10);
	cf = \cf.kr(1).lag(0.5);
	mf = cf * \mf.kr(2);
	mInd = \mInd.kr(1);
	a = GrainFM.ar(
		numChannels: 1, 
		trigger: Impulse.ar(freq: f), 
		dur: overlap/f, 
		carfreq: cf,
		modfreq: mf,
		index: mInd,
	); 

	a = LeakDC.ar(a) * 0.4!2;

	// }).gui;
}).play;
)


( 
Spec.add(\cf, [0,8]);
)




( // bind cf to f
Ndef(\fm_pulsar, { 
	var a,b,c,d,e,f;
	var cf, mf, mInd;
	var overlap;

	overlap	= \overlap.kr(3);

	f = \freq.kr(200);
	cf = f * \cf.kr(1).lag(0.5);
	mf = cf * \mf.kr(2);
	mInd = \mInd.kr(1);
	a = GrainFM.ar(
		numChannels: 1, 
		trigger: Impulse.ar(freq: f), 
		dur: overlap/f, 
		carfreq: cf,
		modfreq: mf,
		index: mInd,
	); 

	a = LeakDC.ar(a) * 0.4!2;

	// }).gui;
}).play;
)




(
SynthDef("fm_pulsar",{

	var a,e,f;
	var t, overlap, carfreq, modfreq, mInd, vel, vel_;

	f = \freq.kr(100);
	f = f + SinOsc.ar(LFNoise1.kr(0.3!2).range(1,4));

	vel = \vel.kr(0.5);
	vel_ = vel.clip(0,1.0).linlin( 0, 1, 2, 0.7);

	e = EnvGen.kr(
		Env.adsr(
			\att.kr(0.025) * vel_,
			\dec.kr(0.02) * vel_, 
			\susl.kr(0.7),
			\rel.kr(4), 
			curve: \curve.kr(-4)
		),
		gate: \gate.kr(1),
		doneAction:2
	) * vel;

	e = e * PinkNoise.ar(1!2).range( 0.1, 1 ).lag(0.02);


	carfreq = f * \cf.kr(2) + LFNoise1.kr(3).range(0,50);
	modfreq = carfreq * \mf.kr(2) +  LFNoise1.kr(3).range(0,100);

	t = Impulse.ar(f);
	mInd = e.pow(3.3) * LFNoise2.kr(5!2).range(\mIndMin.kr(0.1),\mIndMax.kr(2.3));
	overlap = 1.1 - e.pow(0.5) * LFNoise1.kr(0.3!2).range(\olapMin.kr(1),\olapMax.kr(8));
	d = overlap/f;

	a = Mix([
		SinOsc.ar(f),
		GrainFM.ar( 1, t, d, carfreq, modfreq, mInd ),
	]);
	
	a = LeakDC.ar(a * e) ;
	Out.ar(\outBus.kr(0), a * \gain.kr(0.1));

}).add
)


~reverbBus = Bus.audio(s,2);

(
Ndef(\reverb, { 
	var in, a, n;

    in = In.ar(\inBus.ir, 2);
	a = in;

	n = 8;
	n.collect { |i|
		a = AllpassC.ar(
			a, 
			delaytime: LFNoise1.kr(0.03!2).range(0.01 , 0.02 * (i+1)),
			// decaytime: n-i
			decaytime: (i+1)
			// decaytime: (n / 2 -i).abs + 0.1
		).tanh
	}.mean;

	Mix([ 
		in * LFNoise1.kr(3).range(0,1),
		a * 0.5
	]);


}).play;
)

Ndef(\reverb).set(\inBus, ~reverbBus);



( 
	Pbind(
		\instrument, \fm_pulsar,
		\dur, Pseq( 2.pow((-4..1).scramble), inf ),
		\legato, Pfunc({rrand(0.01,1)}),
		\degree, Pseq([0,2,5,6,8,11].scramble, inf),
		\octave, Pstutter(3, Pseq([3,4,5], inf) ),
		\vel, Pstutter(6, Pseq([ 0.2, 1, 1.2 ].scramble, inf)),
		// \cf, 5/4,
		// \mf, 4/3,
		// \mIndMax, 12,
		\outBus, ~reverbBus	
		// \outBus, 0
	).play; 
)




