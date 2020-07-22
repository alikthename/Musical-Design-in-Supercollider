
// sclang side

// running thread can have seed value

thisThread.randSeed = 4;
10.do{1.0.rand2.postln};
12.do{1.0.rand2.postln};
Routine.new({10.do{1.0.rand2.postln};}).play

// Routine can be assigned to certain randSeed

r = Routine.new({10.do{1.0.rand2.postln};})
r.randSeed = 123;
10.do{1.0.rand2.postln};
r.play




// scsynth side

// start a noise patch

(
{
	var noise, filterfreq;
	RandID.ir(0);
	noise = WhiteNoise.ar(0.05 ! 2) + Dust2.ar(70 ! 2);
	filterfreq = LFNoise1.ar(3, 5500, 6000);
	// filterfreq = PinkNoise.ar(3).round(0.1).lag(0.3).range(100, 6000);
	Resonz.ar(noise* 2, filterfreq, 0.5) + (noise * 0.5)
}.play;
)

// reset the seed at a variable rate
(
x = { arg seed=1956;
	// RandID.ir(3);
	RandSeed.ar(Impulse.ar(MouseX.kr(0.1, 100)), seed);
}.play;
)


// above you can see that the sound of the LFNoise1 is not exactly reproduced (filter frequency)
// this is due to interference between the internal phase of the noise ugen and the
// seed setting rate.



x.set(\seed, 2001);
x.set(\seed, 1798);
x.set(\seed, 1902);

// number of internal random generators in scsynth
s.options.numRGens






~seed = 41234586; 

// ~seed = Date.seed; // good way to set a seed

(
thisThread.randSeed = ~seed;

SynthDef(\wtable_vosc_dual_t, {| vel=0.8|
	var env, freq;
	var wtbufnumRange = [ ~wtbufnums.minItem, ~wtbufnums.maxItem  ];
	var sig;
	var fenv;
	var driveDB = \driveDB.kr(0);
	var direct = \direct.kr(0.5).lag(0.2);
	var seed;
	var trg;

	RandID.ir(1);

	// trg = Impulse.kr(1);
	// seed = Demand.kr(trg, 0, Dseq([1111,2222,3333,4444],inf));
	// RandSeed.kr( trg, seed);

	RandSeed.kr( \seedtrg.kr(1), \seed.kr(~seed) );
	trg = Dust.kr(1);

	env = EnvGen.ar(
		Env.adsr( 
			TExpRand.kr(0.001, 0.1, trg), 
			TExpRand.kr(0.001, 0.1, trg), 
			TExpRand.kr(0.1, 0.6, trg), 
			TExpRand.kr(0.5,4,trg), 
			curve: TRand.kr(-5.0,4,trg)), 
		gate:trg, 
		doneAction:0
	).lag(0.1) * vel;

	// env = env * EnvGen.kr(Env.perc(\att.kr(2), \rel.kr(2)), doneAction: 2);

	env = XFade2.ar(env,env * LFDNoise3.ar(env * 20 + 1).range(0,1), env*2-1);

	
	// fenv = env * LFNoise0.kr(TRand.kr(1,21, trg)).range(0.5,1.4).lag(TRand.kr(0.0001,0.1, trg));
	fenv = env * VOsc.ar(
		\fmodbufn.kr( rrand( wtbufnumRange[0],wtbufnumRange[1] ) ).lag(0.4), 
		// TRand.kr(1,21, trg)
		\fmodFreq.kr(11)
	).range(0.5,1.4)
	.lag(TRand.kr(0.0001,0.01, trg));


	freq = \freq.kr(111).lag(0.1);
	// freq = LFNoise0.kr(1).exprange(80,1000).lag(0.1);
	// freq = TExpRand.kr(80,1000,trg);

	sig = VOsc.ar(
		(
			VOsc.ar(\wtmodbufn.kr( rrand( wtbufnumRange[0],wtbufnumRange[1] ) ).lag(0.3), \wtmodfreq.kr(2), Rand(0,2pi), \wtmodamp.kr(1)) 
			+ 
			// ( env * [  Rand(1.5,2),  Rand(1.5,2) ])	
			( env.pow(1.4) * [ LFNoise1.kr(3).range(1.5,3), LFNoise1.kr(3).range(1.5,3)])	
			+ 
			\bufn.ar( rrand( wtbufnumRange[0],wtbufnumRange[1] ) )
		).lag(0.1).mod( wtbufnumRange[1] - wtbufnumRange[0] ) + wtbufnumRange[0],
		freq
	)  * env; 

	sig = MoogVCF.ar(
		sig * driveDB.dbamp, 
		XFade2.ar( env.pow(1.4), fenv.pow(1.4), \fmod.kr(0)) * \fltRange.kr(10000) + 50 , 
		\res.kr(0)
	);
	
	sig = sig * driveDB.neg.dbamp * 0.4; // compensate drive


	Out.ar(\outBus.kr, sig * direct);
	Out.ar(\effBus.kr, sig * (1-direct))
	
	// }).play
}).add
)





~reverbBus = Bus.audio(s,2);





( 
Routine( {
	var a;

	Ndef(\verb, {var a, n;
		RandID.ir(2);
		RandSeed.kr( 1, ~seed );
		a = In.ar(~reverbBus, 2);
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
		a 
	}).play;


	a = [
		Synth(\wtable_vosc_dual_t, [\outBus, 0, \effBus, ~reverbBus, \bufn, ~wtbufnums.choose]),
		Synth(\wtable_vosc_dual_t, [\outBus, 0, \effBus, ~reverbBus, \bufn, ~wtbufnums.choose]),
		Synth(\wtable_vosc_dual_t, [\outBus, 0, \effBus, ~reverbBus, \bufn, ~wtbufnums.choose]),
		// Synth(\wtable_vosc_dual_t, [\outBus, 0, \effBus, ~reverbBus, \bufn, ~wtbufnums.choose]),
	];



	loop{
		a.choose.set(
			\freq,  ([0, 1, 2, 4, 5, 8, 11].choose + 60).midicps * 2.pow((-2..2).choose),
			\wtmodfreq, exprand(1, 20),
			// \wtmodamp, exprand(0.1,4).postln,
			\fmod, rrand(-1.0,1),
			// \fmodbufn, ~wtbufnums.choose,
			\fmodFreq, exprand(2,20),
			// \fltRange, rrand(1000,10000),
			\vel, rrand(0.1,0.9),	
			\direct, rrand(0.1,0.6),
			\seedtrg, [ 0,1 ].choose,
			\seed, rrand(1111,4444)
		);
		2.pow((-2..1).choose).wait;
	}

} ).randSeed_(~seed).play
)





( 
thisThread.randSeed = ~seed;
Ndef(\verb).play;
Pn(
	Pfindur(8,
		Pseed( ~seed,
			Pmono(
				\wtable_vosc_dual_t,
				\dur, Pseq( 2.pow((-3..1).scramble), inf ),
				\legato, Pfunc({rrand(0.01,1.1)}),
				\degree, Pseq([0,2,5,6,8,11].scramble, inf),
				\octave, Pstutter(3, Pseq([3,4,5], inf) ),
				\vel, Pstutter(6, Pseq([ 0.2, 1, 1.5 ], inf)),
				// \seed, 1234567,
				\scale      , Scale.major,
				\root       , 0,
				\gate, Pfunc{[1,0].choose},
				\bufn, Pfunc{~wavetables.choose},
				\fmodbufn, Pfunc{~wavetables.choose},
				\wtmodbufn, Pfunc{~wavetables.choose},
				\effBus,~reverbBus ,
				\att,Pfunc{rrand(0.01,1)},
				\rel,Pfunc{rrand(2.01,1)},
				\direct, Pfunc{rrand(0.1,0.5)},
				\fltRange, Pfunc{rrand(1000,15000)},

			)
		)
	),
	inf
).play;
)




// ~seed = ~seed * 2;

( 
thisThread.randSeed = ~seed;
Ndef(\verb).play;

Pn(
	Pfindur(16,
		Pseed( 
			~seed,
			Pbind( // requires synth release, like // env = env * EnvGen.kr(Env.perc(\att.kr(2), \rel.kr(2)), doneAction: 2);
				\instrument , \wtable_vosc_dual_t,
				// \dur, Pseq( 2.pow((-3..1).scramble), inf ),
				\octave, Pstutter(3, Pseq([3,4], inf) ),
				\vel, Pstutter(2, Pseq([ 0.3, 0.9], inf)),
				\dur        , 2,
				\scale      , Scale.major,
				\root       , 0,
				\degree     , Pfunc{ { rrand(0,14) }!4},
				\bufn, Pfunc{~wavetables.choose},
				\fmodbufn, Pfunc{~wavetables.choose},
				\wtmodbufn, Pfunc{~wavetables.choose},
				\effBus, ~reverbBus,
				\att,Pfunc{rrand(0.01,1)},
				\rel,Pfunc{rrand(2.01,1)},
				\direct, Pfunc{rrand(0.1,0.5)},
				\fltRange, rrand(1000,15000),

			)
		)
	),
	inf

).play;
)




