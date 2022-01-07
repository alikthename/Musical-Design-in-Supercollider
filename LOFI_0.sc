// =====================================================================
// SuperCollider Workspace
// =====================================================================


(
~b = Buffer.readChannel(s, 
	"/mnt/4/sound/Drumdrops Drops In The Bronx/BRONXDROPS-WAV/96-99 bpm/99-RBRK-08.wav", 
	// "/mnt/4/sound/Drumdrops Drops In The Bronx/BRONXDROPS-WAV/96-99 bpm/99-RBRK-04.wav",
	channels:0);
)
~b.play
~b.plot
~b = Buffer.readChannel(s, "/mnt/4/sound/sound_ways/24.11.06/narez--soundways_24_11_06_1otdelenie/loudnorm313.wav", channels:0);

~b.sampleRate












( 
{
	// var in = SinOsc.ar(100);
	var in = PlayBuf.ar(1, ~b.bufnum, BufRateScale.kr(~b.bufnum), loop:1);
	
	var shrate = Array.fill( 8, { |i| 22050 / (i+1) });
	var trig = Impulse.ar(shrate); 
	
	[ in ] ++ Latch.ar(in, trig) * 0.5
}.plot(0.01)
)


Ndef(\stairstep).clear;
( 
Ndef(\stairstep, 
	{
		// var in = PlayBuf.ar(1, ~b.bufnum, BufRateScale.kr(~b.bufnum), loop:1);
		var in = SinOsc.ar(\frq.kr(100));
		
		var sr = \shrate.kr(22050).lag(1);
		var trig = Impulse.ar(sr); 
		
		LinXFade2.ar(
			in,
			Latch.ar(in, trig),
			\drywet.kr(-1)
		)!2;
	}
).play
)






( 
s.scope(2);
Ndef(\stairstep).addSpec(
	\shrate, [110,22050, \exp],
	\lpfscale, [0.1,2],
	\drywet, [-1,1],
).edit
)







// LowPass

( 
{
	var shrate = 300;
	var in = SinOsc.ar(100);
	// var in = PlayBuf.ar(1, ~b.bufnum, BufRateScale.kr(~b.bufnum), loop:1);
	
	var trig = Impulse.ar(shrate); 
	var latch = Latch.ar(in, trig);

	var sig = latch;

	var sigArray = 6.collect { |i| // steep LPF
		sig = LPF.ar(sig, shrate);
	};

	// in.debug;
	[ in ] ++ [ latch ] ++ sigArray

}.plot(0.04)
)








// audio


( 
Ndef(\stairstep, 
	{
		var nfilters = 6;
		var shrate = \shrate.kr(22050).lag(1);
		// var in = SinOsc.ar(100);
		// var in = WhiteNoise.ar(0.1);
		var in = PlayBuf.ar(1, ~b.bufnum, BufRateScale.kr(~b.bufnum), loop:1);
		var sig;


		var trig = Impulse.ar(shrate); 

		sig = Latch.ar(in, trig);


		nfilters.do { |i| // steep LPF
			sig = LPF.ar(sig, ( shrate * \lpfscale.kr( 0.25 ) ).clip(100, SampleRate.ir * 0.45));
		};

		LinXFade2.ar(in, sig, \drywet.kr(1))!2; // phaser effect


	}
).play
)


( 
Ndef(\stairstep).addSpec(
	\shrate, [150,22050, \exp],
	\lpfscale, [0.1,2],
	\drywet, [-1,1],
).edit
)




// Jitter 



( 
Ndef(\stairstep, 
	{
		var nfilters = 6;
		var sig;
		var shrate = \shrate.kr(22050).lag(1);
		var in = PlayBuf.ar(1, ~b.bufnum, BufRateScale.kr(~b.bufnum), loop:1);
		var	dw;


		var trigFreq = LFDNoise3.ar(shrate * \jitterScale.kr(1) !2).range(shrate * ( 1 - \jitter.kr(0.1)), shrate);
		var trig = Impulse.ar(trigFreq); 


		sig = Latch.ar(in, trig);


		nfilters.do { |i| // steep LPF
			sig = LPF.ar(sig, ( shrate * \lpfscale.kr( 0.25 ) ).clip(100, SampleRate.ir * 0.45));
		};

		
		dw = \drywet.kr(1);

		Mix([ 
			in * ( 1 - dw.abs ), 
			sig * dw  
		]);



	}
).play
)

( 
Ndef(\stairstep).addSpec(
	\jitter, [0.0,1],
	\jitterScale, [0.0,1],
	\drywet, [-1,1]
)
)
Ndef(\stairstep).edit;





/// Slew




( 
{
	var shrate = 400;
	// var in = PlayBuf.ar(1, ~b.bufnum, BufRateScale.kr(~b.bufnum), loop:1);
	var in = SinOsc.ar(100);
	
	var trig = Impulse.ar(shrate); 
	var latch = Latch.ar(in, trig);

	var slewrate = 4410 / [1,3,5,10];

	
	var sig = Slew.ar(latch, slewrate, slewrate);

	[ in ] ++ [ latch ] ++ sig

}.plot(0.04)
)






Ndef(\stairstep).clear 

( 
Ndef(\stairstep, 
	{
		var nfilters = 6;
		var sig;
		var shrate = \shrate.kr(22050).lag(1);
		var in = PlayBuf.ar(1, ~b.bufnum, BufRateScale.kr(~b.bufnum), loop:1);
		// var in = WhiteNoise.ar(0.1);  /// for lowpass examples
		var dw;


		var trigFreq = LFDNoise3.ar(shrate * \jitterScale.kr(1) !2).range(shrate * ( 1 - \jitter.kr(0.1)), shrate);
		var trig = Impulse.ar(trigFreq); 


		sig = Latch.ar(in, trig);

		sig = Slew.ar(sig, \slewrate.kr(4410), \slewrate.kr);

		nfilters.do { |i| // steep LPF
			sig = LPF.ar(sig, ( shrate * \lpfscale.kr( 0.25 ) ).clip(100, SampleRate.ir * 0.45), mul:1.2).atan;
		};
		
		sig = HPF.ar(sig, \hpf.kr(100));


		
		dw = \drywet.kr(1);
		Mix([ 
			in * ( 1 - dw.abs ), 
			sig * dw  
		]);


	}
).play;


Ndef(\stairstep).addSpec(
	\jitterScale, [0.0001,1, \exp],
	\slewrate, [20,22050, \exp],
).gui;

)


Ndef('stairstep').set('lpfscale', 0.98372093023256, 'hpf', 4.2498292124156, 'drywet', 0.86046511627907, 'jitterScale', 0.180224551525, 'shrate', 9303.8311061316, 'jitter', 0.43410852713178, 'slewrate', 2512.0911012046);











( 
SynthDef(\stairstep, 
	{
		var nfilters = 6;
		var sig;
		var dw;
		var shrate = \shrate.kr(22050).lag(0.3);
		// var in = PlayBuf.ar(1, buffer, BufRateScale.kr(buffer), startPos: Rand(0.0,1), loop:1);
		var buffer = \buffer.kr;
		var dur = \dur.kr;

		var in = BufRd.ar(
			2, 
			buffer, 
			
			phase: EnvGen.ar(Env.new(
				[ 0, BufFrames.kr(buffer) - 1 ], 
				[ 
					BufDur.kr(buffer) * 
					\stretch.kr(1).lag(4) * 
					LFNoise1.kr(0.3).range(0.98,1.02)
				]
			), 
				gate:1),

			loop:0,
			interpolation: 4
		);




		var trigFreq = LFDNoise3.ar(shrate * \jitterScale.kr(1) !2).range(shrate * ( 1 - \jitter.kr(0.1)), shrate);
		var trig = Impulse.ar(trigFreq); 


		sig = Latch.ar(in, trig);

		sig = Slew.ar(sig, \slewrate.kr(4410), \slewrate.kr);

		nfilters.do { |i| // steep LPF
			sig = LPF.ar(sig, shrate * \lpfscale.kr( 0.5 ), mul: 1.1).atan;
		};
		
		sig = HPF.ar(sig, \hpf.kr(100));
		
		dw = \drywet.kr(1).lag(0.1) + LFNoise1.kr(0.3, mul: 0.3);

		sig = Mix([ 
			in * ( 1 - dw.abs ), 
			sig * dw  
		])  * EnvGen.kr(Env.new([0.0,1.0, 1.0, 0.0], [Rand(0.001,0.3), dur, 0.001 ]), gate:1, doneAction: 2);

		
		Out.ar(
			0,
			sig 
		)

	}
).add

)


( 
~b = [
	"/home/alik/win_f/sound/raimonda_s23min.wav_narez/32.wav",
	"/home/alik/win_f/sound/raimonda_s23min.wav_narez/18.wav",
	"/home/alik/win_f/sound/raimonda_s23min.wav_narez/7.wav",
	"/home/alik/win_f/sound/raimonda_s23min.wav_narez/16.wav",
	"/home/alik/win_f/sound/raimonda_s23min.wav_narez/44.wav",
	"/mnt/4/sound/sound_ways/24.11.06/narez--soundways_24_11_06_1otdelenie/loudnorm313.wav",
	"/mnt/4/sound/sound_ways/24.11.06/narez--soundways_24_11_06_1otdelenie/loudnorm314.wav",
	"/mnt/4/sound/sound_ways/24.11.06/narez--soundways_24_11_06_1otdelenie/loudnorm315.wav",
	"/mnt/4/sound/sound_ways/24.11.06/narez--soundways_24_11_06_1otdelenie/loudnorm316.wav",
	"/mnt/4/sound/sound_ways/24.11.06/narez--soundways_24_11_06_1otdelenie/loudnorm317.wav",
	"/mnt/4/sound/sound_ways/24.11.06/narez--soundways_24_11_06_1otdelenie/loudnorm318.wav"
].collect { |it i| Buffer.read(s,it)};
)



(
thisThread.randSeed = 3456;

Routine ( {
	var dur, x, p, buffer, f;
	var samplerate = 44100;
	12.do { |i|

		i.debug('iteration');

		buffer = ~b.choose(\buf);

		f = if( i < 5, {0}, {[ -0.5, 0, 0.5].choose}); // stretch only after 5th iteration

		dur = buffer.numFrames / samplerate;
		dur = dur * 2.pow(f);
		p = [ 0.5, 0.25, 0.125, 0.0625 ].choose;

		( dur * 0.1 ).wait;
		x = buffer.play;	
		( dur * 0.5 ).wait;
		x.free;
		( dur * 0.1 ).wait;


		"\n".postln;

		x = Synth(\stairstep, [
			\buffer, buffer.bufnum,
			\dur, dur * 1.01,
			\stretch, 2.pow(f).debug(\stretch),
			\slewrate, exprand(900, 4000).debug(\slewrate),
			\shrate, exprand(3000, 15000).debug(\shrate),
			\lpfscale, rrand(0.5,1).debug(\lpfscale),
			\drywet, ( rrand(0.5,1) * [-1,1].choose ).debug(\drywet),
			\jitter, rrand(0.001,0.4).debug(\jitter),
			\jitterScale, exprand(0.01, 0.9).debug(\jitterScale)
		]);
		
		( p * dur ).wait;
		
		'------'.postln;
		x.set(
			\slewrate, rrand(500, 3000).debug(\slewrate),
			\shrate, exprand(3000, 15000).debug(\shrate),
		);
		
		( 1-p * dur ).wait;

		"\n\n\n".postln;
	};
} ).play; 

)







( 
~b = [
	"/mnt/4/sound/Drumdrops Drops In The Bronx/BRONXDROPS-WAV/96-99 bpm/99-RBRK-01.wav",
	"/mnt/4/sound/Drumdrops Drops In The Bronx/BRONXDROPS-WAV/96-99 bpm/99-RBRK-02.wav",
	"/mnt/4/sound/Drumdrops Drops In The Bronx/BRONXDROPS-WAV/96-99 bpm/99-RBRK-03.wav",
	"/mnt/4/sound/Drumdrops Drops In The Bronx/BRONXDROPS-WAV/96-99 bpm/99-RBRK-04.wav",
	"/mnt/4/sound/Drumdrops Drops In The Bronx/BRONXDROPS-WAV/96-99 bpm/99-RBRK-05.wav",
	"/mnt/4/sound/Drumdrops Drops In The Bronx/BRONXDROPS-WAV/96-99 bpm/99-RBRK-08.wav",
].collect { |it i| Buffer.read(s,it)};
)
// ~b[2].play


(

thisThread.randSeed = 3456;

Routine ( {
	var dur, x, p, buffer, f;
	var samplerate = 44100;
	12.do { |i|

		i.debug('iteration');

		buffer = ~b.choose(\buf);

		f = if( i < 5, {0}, {[ -0.5, 0, 0.5].choose}); // stretch only after 5th iteration

		dur = buffer.numFrames / samplerate;
		dur = dur * 2.pow(f);
		p = [ 0.5, 0.25, 0.125, 0.0625 ].choose;

		// ( dur * 0.1 ).wait;
		// x = buffer.play;	
		// ( dur * 0.5 ).wait;
		// x.free;
		// ( dur * 0.1 ).wait;


		"\n".postln;


		x = Synth(\stairstep, [
			\buffer, buffer.bufnum,
			\dur, dur * 1.01,
			\stretch, 2.pow(f).debug(\stretch),
			\slewrate, exprand(1500, 4000).debug(\slewrate),
			\shrate, exprand(5000, 15000).debug(\shrate),
			\lpfscale, rrand(0.7,1.3).debug(\lpfscale),
			\drywet, rrand(0.5,1).debug(\drywet),
			\jitter, rrand(0.001,0.3).debug(\jitter),
			\jitterScale, exprand(0.7, 0.99).debug(\jitterScale)
		]);



		
		( p * dur ).wait;
		
		'------'.postln;
		x.set(
			\slewrate, rrand(1500, 3000).debug(\slewrate),
			\shrate, exprand(8000, 15000).debug(\shrate),
		);
		
		( 1-p * dur ).wait;

		"\n\n\n".postln;
	};
} ).play; 

)
