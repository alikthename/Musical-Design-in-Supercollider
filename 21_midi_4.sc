
MIDIIn.connectAll;


s.latency = nil;            // for immediate response;

MIDIdef.trace(true);
MIDIdef.trace(false);

~wtable_synth = ~wtable_synth ? ();

~wtable_synth[\name] = \wtable_synth;
// ~wtable_synth[\midiSrcID] =  2097152;

Ndef( ~wtable_synth[\name] ).clear;
Ndef( ~wtable_synth[\name] ).ar(2);





(
SynthDef(\wtable_vosc_dual_t, {| vel=0.8, att, rel|
	var env, freq;
	var wtbufnumRange = [ ~wtbufnums.minItem, ~wtbufnums.maxItem  ];
	var sig;
	var fenv;
	var driveDB = \driveDB.kr(0);
	var trg = \gt.kr(0);




	env = EnvGen.ar(
		Env.adsr( 
			TExpRand.kr(att * 0.7, att * 1.5, trg), 
			TExpRand.kr(att * 0.9, att * 1.1, trg), 
			// TExpRand.kr(0.1, 0.6, trg), 
			TExpRand.kr(0.4, 0.7, trg), 
			TExpRand.kr(rel * 0.7, rel * 1.5,trg), 
			curve: TRand.kr(-5.0,4,trg)), 
		gate:trg, 
		doneAction: 2
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
	
	sig = sig * driveDB.neg.dbamp; // compensate drive


	Out.ar( \out.kr, sig );
	
}).add
)


// if Synthdef is not assigned to any variable Synthdesclib can provide info about it (SynthDef().add puts it there)
// SynthDescLib.global[\wtable_vosc_dual_t].controls.do { |it i| it.name.postln};

( 

Spec.add(\bufn, ControlSpec(~wtbufnums.minItem, ~wtbufnums.maxItem, 'linear', 1, 0, ""));
Spec.add(\att, [0.0, 0.1]);
Spec.add(\dec, [0.0, 0.1]);
Spec.add(\rel, [0.0, 4.1]);
Spec.add(\gt, \switch);

Spec.add(\wtmodbufn, \bufn);
Spec.add(\wtmodfreq,\widefreq);
Spec.add(\wtmodamp, [0,5]);

Spec.add(\fmod, \bipolar);
Spec.add(\fmodbufn, \bufn);
Spec.add(\fltRange, \freq); 
Spec.add(\res, \unipolar);
Spec.add(\driveDB, [-40,40,\lin,0,0.01,"dB"]);
Spec.add(\wet150, \unipolar);

)


( 



Ndef( ~wtable_synth[\name] ).prime(\wtable_vosc_dual_t);


Ndef( ~wtable_synth[\name]).put( 150, \filter -> { arg in; 
	var a, n;
	a = in;
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




)














m = NdefMixer(s,30)
ProxyMeter.addMixer(m);

// MIDIdef.noteOn(~wtable_synth[\name]).clear;
// MIDIdef.noteOn(~wtable_synth[\name]).dump;
( 

// poly
MIDIdef.noteOn(
	( ~wtable_synth[\name] ++ "poly_on" ).asSymbol, 
	{ |vel, note, chan| 
		Ndef(~wtable_synth[\name]).put(
			note,
			Ndef(~wtable_synth[\name]).source,
			0, 
			[\freq, note.midicps, \gt, 1, \lev, vel/127]
		) }, 
	chan: 0,
).fix;



MIDIdef.noteOff(
	( ~wtable_synth[\name] ++ "poly_off" ).asSymbol,
	{ |vel, note, chan| 
		Ndef(~wtable_synth[\name]).objects[note].set( \gt, 0); 
		Ndef(~wtable_synth[\name]).removeAt( note );
	}, 
	chan: 0,
).fix;





// PmonoArtic like legato
~notes = List[];
MIDIdef.noteOn(
	( ~wtable_synth[\name] ++ "_on" ).asSymbol, 
	{ |vel, note, chan| 
	var name = ~wtable_synth[\name];	
		if( ~notes.isEmpty, {
			~slot = 1;
			~notes.add(note);
			Ndef(name).put(
				~slot,
				Ndef(name).source,
				0, 
				[\freq, note.midicps, \gt, 1, \lev, vel/127]
			) 
		}, {
			~notes.add(note);
			Ndef(name).objects[~slot].set( \freq, note.midicps ); 
		});
		"On".postln;
		~notes.postln;
	}, 
	chan: 1,
).fix;


MIDIdef.noteOff(
	( ~wtable_synth[\name] ++ "off" ).asSymbol,
	{ |vel, note, chan| 
		var name = ~wtable_synth[\name];	
		if( ~notes.size == 1, {
			Ndef(name).objects[ ~slot ].set( \gt, 0 ); 
			Ndef(name).removeAt( ~slot );
			~notes.remove(note);
		}, {
			~notes.remove(note);
			Ndef(name).objects[ ~slot ].set( \freq, ~notes.last.midicps ); 
		});
		"Off".postln;
		~notes.postln;
	}, 
	chan: 1,
).fix;






///// making copies

~sz = 3;
~names = ~sz.collect( { arg i; ( ~wtable_synth[\name] ++ "_" ++ i  ).asSymbol  });
~names.do{ |it| Ndef( ~wtable_synth[\name] ).copy( it )};

// random ndefs
~playingNdefs = Order.new;


MIDIdef.noteOn(
	( ~wtable_synth[\name] ++ "random_Ndefs_on" ).asSymbol, 
	{ |vel, note, chan| 
		~name = ~names.choose;
		~playingNdefs.put(note, ~name);
		Ndef(~name).put(
			note,
			Ndef(~wtable_synth[\name]).source,
			0, 
			[\freq, note.midicps, \gt, 1, \lev, vel/127]
		) }, 
	chan: 2,
).fix;


MIDIdef.noteOff(
	( ~wtable_synth[\name] ++ "random_ndef_off"  ).asSymbol,
	{ |vel, note, chan| 
		Ndef(~playingNdefs.at(note)).objects[note].set( \gt, 0); 
		Ndef(~playingNdefs.at(note)).removeAt( note );
	}, 
	chan: 2,
).fix;

)


( 
~ccControlled = Order.newFromIndices(
	[
		\bufn,
		\wtmodbufn,
		\fmodbufn,
		\wtmodfreq,
		\wtmodamp, 
		\fmod, 
		\fltRange, 
		\res
	], (21..28)
);







MIDIdef.cc(
	( ~wtable_synth[\name] ++ "_legato_cc" ).asSymbol, 
	{arg val, num;
		var control = ~ccControlled[ num ];
		Ndef( ~note ).set( 
			control, 
			Ndef( ~note ).getSpec(control).map(val/127),
		)
	},
	chan: 1,
	// srcID: nil
).fix; 
)

Ndef('wtable_synth').set('fmodbufn', 141.0, 'bufn', 213.0, 'fmod', 0.32275132275132, 'driveDB', 4.4444444444444, 'fltRange', 2490.3941694701, 'wet150', 0.10582010582011, 'fmodFreq', 1.8345442449618, 'd_time', 0.76379381624215, 'freq', 185.90195797613);


/// chords
Ndef('wtable_synth').set('fmodbufn', 58.0, 'bufn', 67.0, 'fmod', 0.32275132275132, 'driveDB', 4.4444444444444, 'wtmodbufn', 81.0, 'fltRange', 2490.3941694701, 'wtmodamp', 4.4488188976378, 'wet150', 0.10582010582011, 'fmodFreq', 1.8345442449618, 'd_time', 0.76379381624215, 'freq', 185.90195797613, 'wtmodfreq', 3.855981657412);

/// bass
Ndef('wtable_synth').set('fmodbufn', 34.0, 'bufn', 111.0, 'res', 0.37795275590551, 'fmod', -1.0, 'driveDB', 4.4444444444444, 'wtmodbufn', 104.0, 'fltRange', 522.8207675023, 'wtmodamp', 4.8425196850394, 'wet150', 0.10582010582011, 'fmodFreq', 1.8345442449618, 'd_time', 0.76379381624215, 'freq', 185.90195797613, 'wtmodfreq', 0.11008810439184);

Ndef('wtable_synth').set('rel', 17.068384975303, 'fmodbufn', 157.0, 'att', 0.011904778320385, 'bufn', 111.0, 'res', 0.37795275590551, 'fmod', -1.0, 'driveDB', 8.2539682539683, 'wtmodbufn', 104.0, 'fltRange', 522.8207675023, 'vel', 1.0, 'wtmodamp', 4.8425196850394, 'wet150', 1.0, 'fmodFreq', 1.8345442449618, 'd_time', 0.71687116443689, 'freq', 185.90195797613, 'gt', 0.34920634920635, 'wtmodfreq', 0.11008810439184);
