// =====================================================================
// SuperCollider Workspace
// =====================================================================



( 
SynthDef(\grain_0, { |out=0, sendOut, dur=0.1, driveDB=0, in|
	var sig, env;
	
	sig = SoundIn.ar([ in, in + 1 ]);
	
	sig = HPF.ar(sig, \hpf.ar(500));
	// sig = LPF.ar(sig, \lpf.ar(6000));
	
	// env = Env.linen(
	// 	attackTime: dur / 3, 
	// 	sustainTime: dur / 3, 
	// 	releaseTime: dur / 3, 
	// 	level: 1.0, 
	// 	curve: \welch,
	// 	// curve: \sine,
	// 	// curve: -9,
	// 	// curve: 9
	// 	// curve: [0,5,0]
	// );

	// env = Env.sine(dur);
	env = Env.perc(dur * 0.01, dur * 0.99);


	sig = MoogVCF.ar(
		sig * driveDB.dbamp, 
		\lpf.kr(12000), 
		\res.kr(0.5)
	);

	
	
	sig = EnvGen.ar( env, doneAction: 2 ) * sig ;
	
	sig = sig * driveDB.neg.dbamp; // compensate drive
	sig = Balance2.ar(sig[0], sig[1], \pan.kr(0) );
    
	Out.ar(out, sig * \gain.kr(4));
	
}).add;
)

s.latency = nil;





/// use midi messages to start/stop routine/task/pattern

Tdef('grains');

Tdef( 'grains' ).set( 'dur' , 0.90007235870345 );
// Tdef( 'grains' ).set( 'gain' , 60.464212182451 ); /// careful !!!!!!!!!!!!!!
Tdef( 'grains' ).set( 'gain' , 6.464212182451 ); /// careful !!!!!!!!!!!!!!
Tdef( 'grains' ).set( 'hpf' , 70.980837400962 );
Tdef( 'grains' ).set( 'in' , 6 );
Tdef( 'grains' ).set( 'out' , 0.0 );
Tdef( 'grains' ).set( 'res' , 0.30000000000001 );
Tdef( 'grains' ).set( 'speed' , 2.0 );

Tdef( 'grains' ).addSpec( 'speed', [0, 3]);
Tdef( 'grains' ).addSpec( 'dur', [0, 16]);
Tdef( 'grains' ).addSpec( 'res', [0, 1]);
Tdef( 'grains' ).addSpec( 'gain', [0.1, 100, \exp]);
// Tdef( 'grains' ).addSpec( 'sendGain', [0.1, 100, \exp]);
Tdef( 'grains' ).addSpec( 'hpf', \freq);
Tdef( 'grains' ).addSpec( 'in', [0,16] );
Tdef( 'grains' ).addSpec( 'out', [0,16] );

Tdef('grains').gui

Tdef( 'grains' , { arg ev;
		var bps = 60 / 84.271;
		inf.do { arg i;
			var interonset = bps / 2.pow(ev[\speed]); // sec
			s.sendBundle( s.latency, 
				[
					's_new', \grain_0, 
					-1,				// nodeID
					1,				// addAction
					1,				// targetID
					// synth args
					// \dur, interonset * rrand(1.5,4),
					\dur, interonset * ev[\dur],
					// \hpf, rrand(300,1000),
					\hpf, ev[\hpf],
					\lpf, exprand(1200,16000),
					\res, ev[\res],
					\driveDB, rrand(12,18.0),
					\pan, rrand(-0.7,0.7),
					\gain, ev[\gain],
					\in, ev[\in],
					\out, ev[\out],
				]
			);

			interonset.wait;
		}
	} );

(
MIDIdef.noteOn(
	("name" ++"On").asSymbol, 
	{ |vel, note, chan| 
		Tdef(\grains).play;
	}, 
).fix;


MIDIdef.noteOff(
	("name" ++"Off").asSymbol, 
	{ |vel, note, chan| 
		Tdef(\grains).stop;
	}, 
).fix;
)







( 
SynthDef(\grain_0, { |out=0, sendOut, dur=0.1, driveDB=0, in|
	var sig, env;
	
	sig = SoundIn.ar([ in, in + 1 ]);
	
	sig = HPF.ar(sig, \hpf.ar(500));
	// sig = LPF.ar(sig, \lpf.ar(6000));
	

	// env = Env.sine(dur);
	env = Env.perc(dur * 0.01, dur * 0.99);


	sig = MoogVCF.ar(
		sig * driveDB.dbamp, 
		\lpf.kr(12000), 
		\res.kr(0.5)
	);

	
	
	sig = EnvGen.ar( env, doneAction: 2 ) * sig ;
	
	sig = AllpassC.ar( sig,0.2, { Rand(0.01, 0.2) } ! sig.size ); 
	sig = AllpassC.ar( sig,0.2, { Rand(0.01, 0.2) } ! sig.size ); 
	sig = AllpassC.ar( sig,0.2, { LFNoise1.kr(0.3).range(0.002,0.2)} ! sig.size ); 
	sig = AllpassC.ar( sig,0.2, { LFNoise1.kr(0.3).range(0.002,0.2)} ! sig.size ); 

	
	sig = sig * driveDB.neg.dbamp; // compensate drive
	sig = Balance2.ar(sig[0], sig[1], \pan.kr(0) );
    
	Out.ar(out, sig * \gain.kr(4));
    Out.ar(sendOut, sig * \sendGain.kr(4))
}).add;
)


( 
~sz = 4;
~names = ~sz.collect({|i| ( "grains" ++ i ).asSymbol });
~busses = ~sz.collect({|i| Bus.audio(s, 2) });
 )

( 
~sz.do { |i|
	Tdef(~names[i]).play;

	Tdef(~names[i]).set(
		\dur,3, 
		\speed,2, 
		\hpf, 200, 
		\res, 0.3, 
		\gain, 4, 
		\sendGain, 4, 
		\in, i * 2 + 2, 
		\out, i * 2 + 2
	) ;
	

	Tdef( ~names[i] ).addSpec( 'speed', [0, 3]);
	Tdef( ~names[i] ).addSpec( 'dur', [0, 16]);
	Tdef( ~names[i] ).addSpec( 'res', [0, 1]);
	Tdef( ~names[i] ).addSpec( 'gain', [0.1, 100, \exp]);
	Tdef( ~names[i] ).addSpec( 'hpf', \freq);
	Tdef( ~names[i] ).addSpec( 'in', [0,16] );
	Tdef( ~names[i] ).addSpec( 'out', [0,16] );



	Tdef(~names[i],  { arg ev;
		var bps = 60 / 84.271;
		inf.do {
			var interonset = bps / 2.pow(ev[\speed]); // sec
			s.sendBundle( s.latency, 
				[
					's_new', \grain_0, 
					-1,				// nodeID
					0,				// addAction 0 -- head
					0,				// targetID 0 -- before anything
					// synth args
					// \dur, interonset * rrand(1.5,4),
					\dur, interonset * ev[\dur],
					// \hpf, rrand(300,1000),
					\hpf, ev[\hpf],
					\lpf, exprand(1200,16000),
					\res, ev[\res],
					\driveDB, rrand(12,18.0),
					\pan, rrand(-0.7,0.7),
					\gain, ev[\gain],
					\sendGain, ev[\sendGain],
					\in, ev[\in],
					\out, ev[\out],
					\sendOut, ~busses[i].index
				]

			);

			interonset.wait;
		}
	} );    

};

)

~sz.do{ arg i; Tdef(~names[i]).gui.moveTo(300 * i, 700)}






/// accessing Tdef's internal environment

// Tdef(\grains).envir[ \dur ]

( 
//// set them to arbitrary presets:

Tdef( 'grains0' ).set( 'dur' , 2.5514018691589 );
Tdef( 'grains0' ).set( 'gain' , 56.440740313431 );
Tdef( 'grains0' ).set( 'hpf' , 200 );
Tdef( 'grains0' ).set( 'in' , 2 );
Tdef( 'grains0' ).set( 'out' , 2.0 );
Tdef( 'grains0' ).set( 'res' , 0.3 );
Tdef( 'grains0' ).set( 'sendGain' , 43.210420491045 );
Tdef( 'grains0' ).set( 'speed' , 3.0 );

Tdef( 'grains1' ).set( 'dur' , 3.7383177570093 );
Tdef( 'grains1' ).set( 'gain' , 4 );
Tdef( 'grains1' ).set( 'hpf' , 66.741272333731 );
Tdef( 'grains1' ).set( 'in' , 4 );
Tdef( 'grains1' ).set( 'out' , 4.0 );
Tdef( 'grains1' ).set( 'res' , 0.28130841121495 );
Tdef( 'grains1' ).set( 'sendGain' , 4 );
Tdef( 'grains1' ).set( 'speed' , 1.3738317757009 );

Tdef( 'grains2' ).set( 'dur' , 0.89719626168224 );
Tdef( 'grains2' ).set( 'gain' , 8.6797774395154 );
Tdef( 'grains2' ).set( 'hpf' , 200 );
Tdef( 'grains2' ).set( 'in' , 6 );
Tdef( 'grains2' ).set( 'out' , 6.0 );
Tdef( 'grains2' ).set( 'res' , 0.22523364485981 );
Tdef( 'grains2' ).set( 'sendGain' , 10.656891659463 );
Tdef( 'grains2' ).set( 'speed' , 1.7102803738318 );


Tdef( 'grains3' ).set( 'dur' , 3 );
Tdef( 'grains3' ).set( 'gain' , 8.1371290983452 );
Tdef( 'grains3' ).set( 'hpf' , 200 );
Tdef( 'grains3' ).set( 'in' , 8 );
Tdef( 'grains3' ).set( 'out' , 8.0 );
Tdef( 'grains3' ).set( 'res' , 0.3 );
Tdef( 'grains3' ).set( 'sendGain' , 6.0876177392717 );
Tdef( 'grains3' ).set( 'speed' , 2 );

)







( 
~sz.do { |i|
	Ndef(( "verb" ++ i ).asSymbol).ar(2);
	Ndef(( "verb" ++ i ).asSymbol).prime({
		var a, n;
		a = In.ar(~busses[i], 2);
		n = 8;
		n.collect { |i|
			a = AllpassC.ar(
				a, 
				delaytime: LFNoise1.kr(0.03!2).range(0.01 , 0.02 * (i+1)),
				// decaytime: n-i
				decaytime: (i+1) * \d_time.kr(1)
				// decaytime: (n / 2 -i).abs + 0.1
			).tanh
		}.mean;
		a 
	});    
};

Spec.add(\d_time, [ 0.01, 0.2, \exp ])

)






// Ndef(( "verb" ++ 3 ).asSymbol).ar(2);
// Ndef(( "verb" ++ 3 ).asSymbol).clear;
// Ndef(( "verb" ++ 3 ).asSymbol).play;

// Ndef(( "verb" ++ 3 ).asSymbol).prime({
// 	var a, n;
// 	a = In.ar(~busses[3], 2);
// 	n = 8;
// 	n.collect { |i|
// 		a = AllpassC.ar(
// 			a, 
// 			delaytime: LFNoise1.kr(0.03!2).range(0.01 , 0.02 * (i+1)),
// 			// decaytime: n-i
// 			decaytime: (i+1) * \d_time.kr(1)
// 			// decaytime: (n / 2 -i).abs + 0.1
// 		).tanh
// 	}.mean;
// 	a 
// });    




