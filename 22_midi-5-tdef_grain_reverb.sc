

( //envelopes
SynthDef(\grain_0, { |out=0, dur=0.1, driveDB=0|
	var sig, env;
	
	sig = SoundIn.ar([0,1]);
	
	sig = HPF.ar(sig, \hpf.ar(500));
	// sig = LPF.ar(sig, \lpf.ar(6000));
	
	env = Env.linen(
		attackTime: dur / 3, 
		sustainTime: dur / 3, 
		releaseTime: dur / 3, 
		level: 1.0, 
		curve: \welch,
		// curve: \sine,
		// curve: -9,
		// curve: 9
		// curve: [0,5,0]
	);

	// env = Env.sine(dur);
	// env = Env.perc(dur * 0.01, dur * 0.99);


	// sig = sig.distort;
	
	sig = MoogVCF.ar(
		sig * driveDB.dbamp, 
		\lpf.kr(1200), 
		\res.kr(0.5)
	);

	
	sig = AllpassC.ar( sig,0.2, { Rand(0.01, 0.2) } ! sig.size ); 
	// sig = AllpassC.ar( sig,0.2, { Rand(0.01, 0.2) } ! sig.size ); 

	
	sig = EnvGen.ar( env, doneAction: 2 ) * sig ;
	sig = sig * driveDB.neg.dbamp; // compensate drive
	// sig = Balance2.ar(sig[0], sig[1], \pan.kr(0) );

    Out.ar(out, sig * 0.7)
}).add;
)







( 
// s.latency = 0.15;
// s.latency = 1024 * 6 / 44100;
Tdef(\grains,  {
	// var interonset = 1024 / 44100; // sec
	// var interonset = 0.1; // sec
	var interonset = 60 / 84.271 / 2.pow(4); // sec
	inf.do { arg i;

		s.sendBundle( s.latency, 
			[
				's_new', \grain_0, 
				-1,				// nodeID
				1,				// addAction
				1,				// targetID
				// synth args
				// \dur, interonset * rrand(1.5,4),
				\dur, interonset * 12,
				// \hpf, rrand(300,1000),
				\hpf, 400,
				\lpf, exprand(1200,6000),
				\res, ~res,
				\driveDB, rrand(12,18.0),
				\pan, rrand(-0.7,0.7),
			]
		);

		interonset.wait;
	}
} ).play;
)

MIDIdef.noteOn(
	("name" ++"On").asSymbol, 
	{ |vel, note, chan| 
		~res = vel / 127;
		
		//synthsArray.put(note,this.synth) 
	}, 
).fix;