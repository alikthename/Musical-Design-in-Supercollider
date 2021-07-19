
MIDIIn.connectAll;


s.latency = nil;            // for immediate response;

MIDIdef.trace(true);
MIDIdef.trace(false);

~grainsin_pulsar = ~grainsin_pulsar ? ();

~grainsin_pulsar[\name] = \grainsin_pulsar;
~grainsin_pulsar[\midiChannel] = 0;
// ~grainsin_pulsar[\midiSrcID] =  2097152;

Ndef( ~grainsin_pulsar[\name] ).clear;
Ndef( ~grainsin_pulsar[\name] ).ar(2);








//// prime synthdef






(
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
			// attackTime: ( f_.linlin( 50, 1500, 0.02, 0.005) ),
			attackTime: 0.001,
			releaseTime: Rand(0.1,3.3) 
			// releaseTime: 0.5,
		), 
		gate: \gt.kr(1),
		doneAction: 2
	);

	// c = e * SinOsc.ar(f*3!2);	//test

	e = e * PinkNoise.ar(1!2).range( 0.1, 1 ).lag(0.02) ;

// c = WhiteNoise.ar(0.2!2);
// c = SinOsc.ar(100,0,0.5!2);

	a = GrainSin.ar(
		numChannels: 1, 
		trigger: Impulse.ar(f), 
		// dur:  overlapA * ( 1.05 - e.pow(0.5) )
		dur:  overlapA * ( 0.05 + e.pow(0.5) )
		* SinOsc.ar(e * 6, {Rand(0, 6.28)}.dup).range(0.87,1.15) 
		/ f, 
		freq: f * 
		( formantA  * SinOsc.ar(7 * e, {Rand(0, 6.28)}.dup).range(0.9,1.1) ),
		pan: PinkNoise.ar(0.5!2), 
	);


	b = GrainSin.ar(
		numChannels: 1, 
		trigger: Impulse.ar(f + Rand(-3,3)), 
		// dur:  overlapB * ( 1.05 - e.pow(0.5) ) 
		dur:  overlapB * ( 0.05 + e.pow(0.5) ) 
		*SinOsc.ar(e * 2, {Rand(0, 6.28)}.dup).range(0.87,1.15)  
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
		*SinOsc.ar(e * 6, {Rand(0, 6.28)}.dup).range(0.87,1.15)  
		/ f,
		freq: f 
		* SinOsc.ar(e * 8, {Rand(0, 6.28)}.dup).range(0.9,Rand(1,1.5))
		,
		pan: PinkNoise.ar(0.5!2), 
	);

	c = Mix([
		c * ( e ).pow(0.7),
		a * ( \levA.kr(1) * e ).pow(f_.linlin( 50, 1500, 1.1, 1.5)) * lev,
		b * ( \levB.kr(0.8) * e ).pow(f_.linlin( 50, 1500, 1.5, 2.5)) * lev,
	]);


	c = LeakDC.ar(c* 0.45);

	
	
    Out.ar(out, c)
}).add;
)




( 
Ndef( ~grainsin_pulsar[\name]).prime( \sin_pulsar ); 

/// .....prime() is same as 
// Ndef( ~grainsin_pulsar[\name], \sin_pulsar) 
// But without starting the synth


//////// reverb

Ndef( ~grainsin_pulsar[\name] ).put( 150, \filter -> { arg in;
	var d = in;
	var diffusion = \diffusion.kr(1);
	var spacesize = \spacesize.kr(0.1);
	8.do { |i|
		d = AllpassC.ar(
			d,
			delaytime: LFNoise1.kr(0.01!2).range(0.01, spacesize), 
			decaytime: diffusion 
		)
	};	

	d
 
});



//////// SPECS

Ndef( ~grainsin_pulsar[\name]).addSpec(
	\lev, \amp,
	\freq, \freq,
	\formantA, [1,7],
	\formantB, [1,7],
	\overlapA, [0.01, 20,\exp],
	\overlapB, [0.01, 20,\exp],
	\levA, [0.01, 3,\exp],
	\levB, [0.01, 3,\exp],
	\gt, \gt,
	\diffusion, [0.0001, 4],
	\spacesize, [0.02, 0.2],
).edit;



)





)

// Ndef(~grainsin_pulsar.name).objects.first.controlNames.do({arg it; it.name.postln})
Ndef(~grainsin_pulsar.name).objects



////////// MIDIdefs



// MIDIdef.noteOn(~grainsin_pulsar[\name]).clear;
// MIDIdef.noteOn(~grainsin_pulsar[\name]).dump;

( 
MIDIdef.noteOn(
	( ~grainsin_pulsar[\name] ++ "_On").asSymbol, 
	{ |vel, note, chan| 
		Ndef(~grainsin_pulsar[\name]).put(
			note,
			Ndef(~grainsin_pulsar[\name]).source,
			0, 
			[\freq, note.midicps, \gt, 1, \lev, vel/127]
		);
		\on.postln;
	}, 
	chan: ~grainsin_pulsar[\midiChannel],
	// srcID:8388608
).fix;



MIDIdef.noteOff(
	( ~grainsin_pulsar[\name] ++ "_Off" ).asSymbol, 
	{ |vel, note, chan| 
		Ndef( ~grainsin_pulsar[\name] ).removeAt( note ); 
	}, 
	chan: ~grainsin_pulsar[\midiChannel],
).fix;





~ccControlled = Order.newFromIndices(
	[
		\lev, \freq, \formantA, \formantB, \overlapA, \overlapB, \levA, \levB, 
	], (21..28)
);





// Ndef( ~grainsin_pulsar[\name] ).getSpec(\freq).map(1)

MIDIdef.cc(
	( ~grainsin_pulsar[\name] ++ "_CC" ).asSymbol, 
	{arg val, num;
		var control = ~ccControlled[ num ];

		Ndef(~grainsin_pulsar[\name]).set( 
			control, 
			Ndef( ~grainsin_pulsar[\name] ).getSpec(control).map(val/127)
		)

		// Ndef( ~grainsin_pulsar[\name] ).softSet( 
		// 	control, 
		// 	Ndef( ~grainsin_pulsar[\name] ).getSpec(control).map(val/127),
		// 	mapped: true,
		// 	spec: Ndef( ~grainsin_pulsar[\name] ).getSpec( control )
		// )

	},
	ccNum: ~ccControlled.indices,
	chan: ~grainsin_pulsar.midiChannel,
	// srcID: nil
).fix; 


)




///// presets

Ndef('grainsin_pulsar').set('levA', 0.30271236992455, 'formantA', 3.5714285714286, 'overlapB', 0.40443672029499, 'levB', 0.28498187848462, 'lev', 0.81859410430839, 'formantB', 4.2698412698413, 'overlapA', 2.1898421014008);



Ndef('grainsin_pulsar').set('levA', 0.49765180538124, 'formantA', 6.007874015748, 'levB', 0.31759720095202, 'spacesize', 0.16952380952381, 'overlapB', 1.0651340032941, 'wet150', 0.34391534391534, 'lev', 0.19443238886478, 'formantB', 3.2204724409449, 'diffusion', 1.6931793650794, 'overlapA', 1.8253052748325, 'freq', 69.877455494983);


