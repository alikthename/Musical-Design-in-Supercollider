MIDIIn.connectAll;


s.latency = nil;            // for immediate response;

MIDIdef.trace(true);
MIDIdef.trace(false);

~granular_sampler = ~granular_sampler ? ();

~granular_sampler[\name] = \granular_sampler;
~granular_sampler[\midiChannel] = 1;



//// init-set instr /// begin


Ndef( ~granular_sampler[\name] ).clear;
Ndef( ~granular_sampler[\name] ).ar(2);

//// init-set instr /// begin


(

~granular_sampler[\b].free;
~granular_sampler[\b] = Buffer.readChannel(s, 
	"/samplepath/auiofile.wav",
	// "/othersamplepath/otherauiofile.wav",
	channels:[0], action: {arg buffer;
		SynthDef(\granular_sampler, { |tFreq=20, overlap=2, rate=1, posRate=1, 
			tFreqMF=0, tFreqMD=0, 
			posRateMF=0, posRateMD=0,
			rateMF=0, rateMD=0|

			var phasor, gran, env; 
			var bufFrames = BufFrames.ir(buffer); 
			var e, t;

			var tFreqMod = {
				// SinOsc.ar(tFreqMF, Rand(0.0,2pi)) * tFreqMD;
				LFDNoise0.ar(tFreqMF) * tFreqMD;
			};
			
			var posRateMod = {
				SinOsc.ar(posRateMF, Rand(0.0,2pi)) * posRateMD;
				// LFDNoise0.ar(posRateMF) * posRateMD;
			};
			
			var rateMod = {
				SinOsc.ar(rateMF, Rand(0.0,2pi)) * rateMD;
				// LFDNoise0.ar(rateMF) * rateMD;
			};
			
			tFreq = tFreq + tFreqMod.dup;
			posRate = posRate + posRateMod.dup;
			rate = rate + rateMod.dup;
			
			e = EnvGen.kr(
				Env.adsr( 
					\att.kr(0.001), 
					\dec.kr(0.01), 
					\susl.kr(1), 
					\rel.kr(0.1) 
				), 
				gate: \gt.kr(0), 
				doneAction: 2
			);
			
			t = Impulse.ar(tFreq);

			// t = Impulse.ar(tFreq) + Dust.ar(tFreq);
			// t = Dust.ar(tFreq);

			phasor = Phasor.ar(
				trig: 0.0, 
				rate: posRate, 
				// start: 0.0, 
				start: \pos.kr(0) * bufFrames, 
				end: bufFrames, 
				resetPos: 0.0
			);

			gran = GrainBuf.ar(
				numChannels: 1, 
				trigger: t, 
				dur: tFreq.reciprocal * overlap, 
				sndbuf: buffer, 
				rate: rate, 
				pos: phasor / bufFrames, 
				// pos: Integrator.ar( K2A.ar(posRate) ) / BufFrames.ir(buffer), // withoout phasor
				interp: 2, 
				pan: 0, 
				envbufnum: -1, 
				maxGrains: 512, 
			);

			gran = gran * e  * 0.5;
			Out.ar(\out.kr, gran);

		}).add
	});


)






( 
Ndef(~granular_sampler[\name]).play;
Ndef(~granular_sampler[\name]).prime( \granular_sampler);



Ndef( ~granular_sampler[\name]).put( 150, \filter -> {arg in;
	var local;
	var attack = \attack.kr(0.01);
	var release = \release.kr(0.1);
	var dry, t, o, r, c;
	t = \threshhold.kr(-6);
	
	// in = Decay2.kr(Impulse.kr(2),0.05,0.5) ;
	
	in = in * \pregain.kr(1);
	a = EnvDetect.ar(in.mean, attack, release);
	
	// how much we are over by
	o = a.ampdb.excess(t);
	
	// scale the excess value by ratio
	r = \ratio.kr(4);
	
	c = ( max(o, 0.0) ) * (r.reciprocal - 1);
	c = c.dbamp;
	in = in * c * \gain.kr(9).dbamp;
	in = in.softclip ;

	in 
	

} );



Ndef( ~granular_sampler[\name]).put( 160, \filter -> { arg in;
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





)




Spec.add(\overlap, [0.001, 20, \exp]);
Spec.add(\rate, [-4, 4]);
Spec.add(\posRate, [0, 4]);
Spec.add(\tFreq, \widefreq);
Spec.add(\tFreqMF, [0.0,20]);
Spec.add(\tFreqMD, [0.0,20]);
Spec.add(\posRateMF, [0.0,2]);
Spec.add(\posRateMD, [0.0,2]);
Spec.add(\rateMF, [0.0,20]);
Spec.add(\rateMD, [0.0,2]);
Spec.add(\att, [0.0,1]);
Spec.add(\dec, [0.0,0.1]);
Spec.add(\susl, [0.0,1.0]);
Spec.add(\rel, [0.0,4]);
Spec.add(\gt, [0.0,1]);


Spec.add(\attack, [0.0000001,0.1, \exp]);
Spec.add(\release, [0.0000001,0.4, \exp]);
Spec.add(\threshhold, [0,-120]);
Spec.add(\dryLevel, [0,10]);
Spec.add(\pregain, [0,100]);
Spec.add(\gain, [0,100]);
Spec.add(\ratio, [1,20]);

Spec.add(\diffusion, [0.0001, 4]);
Spec.add(\spacesize, [0.02, 1.2]);



// Ndef(~granular_sampler[\name]).dump
m = NdefMixer(s, 25);
ProxyMeter.addMixer(m)



// Ndef(~granular_sampler.name).objects.first.controlNames.do({arg it; it.name.postln})

// MIDIdef.noteOn.clear;
// MIDIdef.noteOn(~granular_sampler[\name]).clear;
// MIDIdef.noteOn(~granular_sampler[\name]).dump;

( 
MIDIdef.noteOn(
	( ~granular_sampler[\name] ++ "noteOn" ).asSymbol, 
	{ |vel, note, chan| 
		Ndef(~granular_sampler[\name]).put(
			note,
			Ndef(~granular_sampler[\name]).source,
			0, 
			[\pos, note.linlin( 48, 95, 0.0, 1.0 ), \gt, 1, \lev, vel/127]
		);
		note.postln;
	}, 
	noteNum: (48..95),
	chan: ~granular_sampler[\midiChannel],
).fix;







MIDIdef.noteOff(
	(~granular_sampler[\name] ++ "noteOff").asSymbol, 
	{ |vel, note, chan| 
		Ndef( ~granular_sampler[\name] ).objects[note].set( \gt, 0); 
		Ndef( ~granular_sampler[\name] ).removeAt( note );
		\off.postln;
	}, 
	chan: ~granular_sampler[\midiChannel],
	// srcID: nil
).fix;




~granular_sampler[\ccControlled] = Order.newFromIndices(
	[
		\overlap, 
		\rate, 
		\posRate, 
		\tFreq, 
		\tFreqMF, 
		\tFreqMD, 
		\posRateMF, 
		\posRateMD
	], (21..28)
);





// MIDIdef.cc(( ~granular_sampler[\name] ++ "_CC" ).asSymbol).clear;





MIDIdef.cc(
	( ~granular_sampler[\name] ++ "_CC" ).asSymbol, 
	{arg val, num;
		var control = ~granular_sampler[\ccControlled][ num ];
		Ndef( ~granular_sampler[\name] ).set( 
			control, 
			Ndef( ~granular_sampler[\name] ).getSpec(control).map(val/127),
		)
		
		// Ndef( ~granular_sampler[\name] ).softSet( 
		// 	control, 
		// 	Ndef( ~granular_sampler[\name] ).getSpec(control).map(val/127),
		// 	mapped: true,
		// 	// spec: Ndef( ~granular_sampler[\name] ).getSpec( control ).asSpec
		// 	spec: Ndef( ~granular_sampler[\name] ).getSpec( control )
		// )
	},
	ccNum: ~granular_sampler[\ccControlled].indices,
	chan: ~granular_sampler.midiChannel,
	// srcID: nil
).fix; 
)



//// presets ---begin

/// this one is good on something
// Ndef('granular_sampler').set('posRateMF', 0.0, 'rateMF', 4.1269841269841, 'tFreqMF', 17.037037037037, 'tFreqMD', 4.2328042328042, 'rate', 1.86, 'overlap', 4.0, 'rateMD', 0.016931216931217);



/// on guitar
Ndef('granular_sampler').set('rel', 2.6455026455026, 'att', 0.97883597883598, 'posRateMF', 0.83597883597884, 'posRateMD', 0.15873015873016, 'rateMF', 0.63492063492063, 'wet150', 1.0, 'tFreqMF', 13.650793650794, 'tFreqMD', 8.4656084656085, 'wet160', 0.26984126984127, 'rateMD', 0.010582010582011, 'overlap', 3.7394977549113, 'posRate', 0.23280423280423);

// tempo tuned
Ndef('granular_sampler').set('rel', 0.2962962962963, 'att', 0.97883597883598, 'rate', 0.76, 'spacesize', 0.14761904761905, 'posRateMF', 0.41269841269841, 'posRateMD', 0.0, 'wet150', 1.0, 'wet160', 0.26984126984127, 'posRate', 0.062992125984252, 'susl', 1.0, 'dec', 0.0, 'tFreq', 2.85, 'rateMF', 1.6931216931217, 'tFreqMF', 2.4338624338624, 'tFreqMD', 0.0, 'rateMD', 0.010582010582011, 'overlap', 0.90861608405079, 'diffusion', 1.6508523809524, 'gt', 0.0, 'freq', 1729.9672025953);

// stutter
Ndef('granular_sampler').set('rel', 0.71957671957672, 'att', 0.97883597883598, 'tFreq', 4.8179985536702, 'posRateMF', 0.0, 'posRateMD', 0.0, 'rateMF', 0.0, 'wet150', 1.0, 'tFreqMF', 0.0, 'tFreqMD', 0.0, 'wet160', 0.26984126984127, 'rateMD', 0.010582010582011, 'overlap', 0.73680629972808, 'posRate', 0.021164021164021);

// Ndef.all.at(\localhost).envir.do { |it| it.clear};




// dr loop


Ndef('granular_sampler').set('rel', 0.021164021164021, 'att', 0.0, 'dec', 0.0, 'rate', 0.95767195767196, 'spacesize', 0.02952380952381, 'tFreq', 2086.2088059687, 'posRateMF', 1.4814814814815, 'posRateMD', 0.021164021164021, 'rateMF', 3.7037037037037, 'tFreqMF', 16.402116402116, 'tFreqMD', 13.544973544974, 'wet150', 1.0, 'wet160', 0.031746031746032, 'rateMD', 0.021164021164021, 'overlap', 2.0840351326575, 'diffusion', 0.2540619047619, 'gt', 0.0, 'posRate', 0.8042328042328, 'susl', 1.0);

//// presets ---end