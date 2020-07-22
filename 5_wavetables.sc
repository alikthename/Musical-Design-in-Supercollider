
( // run once to convert and resample wavetable files
var paths, file, data, n, newData, outFile;
paths = "/mnt/4/sound/wtables/AKWF/AKWF_0002/*.wav".pathMatch;
Routine({
	paths.do { |it i|
		// 'protect' guarantees the file objects will be closed in case of error
		protect {
			// Read original size of data
			file = SoundFile.openRead(paths[i]);
			data = Signal.newClear(file.numFrames);
			file.readData(data);
			0.1.wait;
			// Convert to n = some power of 2 samples.
			// n = data.size.nextPowerOfTwo;
			n = 4096;
			newData = data.resamp1(n);
			0.1.wait;
			// Convert the resampled signal into a Wavetable.
			// resamp1 outputs an Array, so we have to reconvert to Signal
			newData = newData.as(Signal).asWavetable;
			0.1.wait;

			// save to disk.
			outFile = SoundFile(paths[i] ++ "_4096.wtable")
			.headerFormat_("WAV")
			.sampleFormat_("float")
			.numChannels_(1)
			.sampleRate_(44100);
			if(outFile.openWrite.notNil) {
				outFile.writeData(newData);
				0.1.wait;
			} {
				"Couldn't write output file".warn;
			};
		} {
			file.close;
			if(outFile.notNil) { outFile.close };
		};

	}
}).play

)


(	// to be put to Platform.userConfigDir +/+ "startup.scd"

~serverBootFunc = { |server|

        var wtsize, wtpaths, wtbuffers;
	
	"-----------wavetables begin-----------".postln;	


	wtsize = 4096;
	wtpaths = "/mnt/4/sound/wtables/AKWF/AKWF_0002/4096/*".pathMatch;
	// wtbuffers = Buffer.allocConsecutive(wtpaths.size, s, 2048, 1, );
	wtbuffers = Buffer.allocConsecutive(wtpaths.size, s, wtsize * 2, 1, );
	wtpaths.do { |it i| wtbuffers[i].read(wtpaths[i])};


	~wtbufnums = List[];
	~wavetables = ();


	wtpaths.do { |it i|
		var name = wtbuffers[i].path.basename.findRegexp(".*\.wav")[0][1].splitext[0];
		var buffer = wtbuffers[i].bufnum;
		~wavetables[name.asSymbol] = buffer;
		~wtbufnums.add(buffer);
	};


	"-----------wavetables end-----------".postln;	
};

ServerBoot.add(~serverBootFunc, \default);
)



( 

Spec.add(\bufn, ControlSpec(~wtbufnums.minItem, ~wtbufnums.maxItem, 'linear', 1, 0, ""));

Spec.add(\wtmodbufn, \bufn);
Spec.add(\wtmodfreq,\widefreq);
Spec.add(\wtmodamp, [0,5]);

Spec.add(\fmod, \bipolar);
Spec.add(\fmodbufn, \bufn);
Spec.add(\fltRange, \freq); 
Spec.add(\res, \unipolar);
Spec.add(\driveDB, [-40,40,\lin,0,0.01,"dB"]);
)

Ndef(\wtable_vosc_dual_t).edit

( 
Ndef(\wtable_vosc_dual_t, {
	var env, freq;
	var wtbufnumRange = [ ~wtbufnums.minItem, ~wtbufnums.maxItem  ];
	var sig;
	// var trg = Dust.kr(2);
	var trg = Impulse.kr(0.5);
	// trg = trg.lag(TRand.kr(0.0,0.2,trg));

	env = EnvGen.ar(Env.perc(
		TRand.kr(0.2,1,trg), 
		TRand.kr(0.2,2,trg)
	), gate:trg).lag(0.1);
	


	// env = XFade2.ar(env,env * LFDNoise3.ar(env * 20 + 1).range(0,1), env*2-1);
	
	freq = TExpRand.kr(80,500,trg);

	sig = VOsc.ar(
		(
			 VOsc.ar(\wtmodbufn.kr( rrand( wtbufnumRange[0],wtbufnumRange[1] ) ).lag(0.3), \wtmodfreq.kr(2), Rand(0,2pi), \wtmodamp.kr(1)) 
			+ 
			env.pow(1.4)
			+ 
			\bufn.ar( rrand( wtbufnumRange[0],wtbufnumRange[1] ) )
		) .lag(1.8) .mod( wtbufnumRange[1] - wtbufnumRange[0] ) + wtbufnumRange[0],
		freq
	)  * env; 
	


	sig!2 * 0.2
	
}).play


)

(// low pass filtering the env
{
	[
		EnvGen.kr(Env.perc(0.02, 0.02)),
		EnvGen.kr(Env.perc(0.02, 0.02)).lag(0.1)
	]
}.plot(0.1)
)

({ // modulo plot
var a = Line.ar(8,150);
[
	a,
	a.mod(107-8) +8
]
}.plot(1))





Ndef(\wtable_vosc_dual_t_0).edit.nameView

(
Ndef(\wtable_vosc_dual_t_0, {| vel=1|
	var env, freq;
	var wtbufnumRange = [ ~wtbufnums.minItem, ~wtbufnums.maxItem  ];
	var sig;
	var fenv;
	var driveDB = \driveDB.kr(0);
	var direct = \direct.kr(0.5);
	// var trg = \trg.kr(1);
	// var trg = Dust.kr(2);
	var trg = Impulse.kr(1);


	env = EnvGen.ar(
		Env.adsr( 
			TExpRand.kr(0.001, 0.1, trg), 
			TExpRand.kr(0.001, 0.1, trg), 
			TExpRand.kr(0.1, 0.6, trg), 
			TExpRand.kr(1.5,4,trg), 
			curve: TRand.kr(-5.0,4,trg)
		), 
		gate:trg, 
		doneAction:0
	).lag(0.1) * vel;


	env = XFade2.ar(env,LFDNoise3.ar(env * 20 + 1).range(0,1), env*2-1);

	// env = env * WhiteNoise.ar(MouseX.kr(0,1)!2).range(0,1.0);
	
	// fenv = env * LFNoise0.kr(TRand.kr(1,21, trg)).range(0.5,1.4).lag(TRand.kr(0.0001,0.1, trg));
	fenv = env * VOsc.ar(
		\fmodbufn.kr( rrand( wtbufnumRange[0],wtbufnumRange[1] ) ).lag(0.4), 
		\fmodFreq.kr(11)
	).range(0.5,1.4)
	.lag(TRand.kr(0.0001,0.01, trg));


	// freq = \freq.kr(111).lag(0.1);
	// freq = LFNoise0.kr(1).exprange(80,1000).lag(0.1);
	freq = TExpRand.kr(80,1000,trg);

	sig = VOsc.ar(
		(
			VOsc.ar(\wtmodbufn.kr( rrand( wtbufnumRange[0],wtbufnumRange[1] ) ).lag(0.3), \wtmodfreq.kr(2), Rand(0,2pi), \wtmodamp.kr(1)) 
			+ 
			( env * [  Rand(0.5,1.5),  Rand(0.5,1.5) ])	
			// ( env.pow(1.4) * [ LFNoise1.kr(3).range(1.5,9), LFNoise1.kr(3).range(1.5,9)] + 0.1)	
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


	sig * direct;
	
}).play
)


(
SynthDef(\wtable_vosc_dual_t, {| vel=0.8|
	var env, freq;
	var wtbufnumRange = [ ~wtbufnums.minItem, ~wtbufnums.maxItem  ];
	var sig;
	var fenv;
	var driveDB = \driveDB.kr(0);
	var direct = \direct.kr(0.5).lag(0.2);
	// var trg = \trg.kr(1);
	var trg = Dust.kr(1);
	// var trg = Impulse.kr(2);


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


	env = XFade2.ar(env,env * LFDNoise3.ar(env * 20 + 1).range(0,1), env*2-1);

	// env = env * WhiteNoise.ar(MouseX.kr(0,1)!2).range(0,1.0);
	
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
{
	var a;

	Ndef(\verb, {var a, n;
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
		);
		2.pow((-2..1).choose).wait;
	}

}.fork
)


