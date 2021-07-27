// =====================================================================
// SuperCollider Workspace
// =====================================================================

MIDIIn.connectAll;



MIDIdef.trace(true);
MIDIdef.trace(false);











( 
Routine ( {
    
	s.latency = nil;
	~samplers = ();



	[\kk, \sn, \hh, \perc].do { |it| ~samplers[it] = ()};

	~samplers[\kk][\paths] = [ 
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/KIKS/KICK-03.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/KIKS/KICK-04.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/KIKS/KICK-05.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/KIKS/KICK-06.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/KIKS/KICK-07.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/KIKS/KICK-08.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/KIKS/KICK-09.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/KIKS/KICK-10.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/KIKS/KICK-11.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/KIKS/KICK-12.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/KIKS/KICK-13.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/KIKS/KICK-14.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/KIKS/KICK-15.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/KIKS/KICK-16.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/KIKS/KICK-17.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/KIKS/KICK-18.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/KIKS/KICK-19.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/KIKS/KICK-20.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/KIKS/KICK-23.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/KIKS/KICK-24.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/TOMS/TOM-1.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/TOMS/TOM-2.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/TOMS/TOM-3.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/TOMS/TOM-4.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/TOMS/TOM-5.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/TOMS/TOM-6.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/TOMS/TOM-7.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/TOMS/TOM-8.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/TOMS/TOM-9.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/TOMS/TOM-10.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/TOMS/TOM-11.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/TOMS/TOM-12.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/TOMS/TOM-13.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/TOMS/TOM-14.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/TOMS/TOM-15.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/TOMS/TOMFILL-01.wav",
	];

	~samplers[\perc][\paths] = [
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/CLAPS/CLAPS 01.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/CLAPS/CLAPS 02.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/CLAPS/CLAPS 03.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/CLAPS/CLAPS 04.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/CLAPS/CLAPS 05.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/CLAPS/CLAPS 06.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/CLAPS/CLAPS 07.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/CLAPS/CLAPS 08.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/CLAPS/CLAPS 09.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/CLAPS/CLAPS 10.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/CLAPS/CLAPS 11.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/CLAPS/CLAPS 12.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/CLAPS/CLAPS 13.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/CLAPS/CLAPS 14.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/CLAPS/CLAPS 15.wav",
	];


	~samplers[\sn][\paths] = [
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/SNARES/JAZZSNARE-01.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/SNARES/JAZZSNARE-02.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/SNARES/JAZZSNARE-03.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/SNARES/JAZZSNARE-04.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/SNARES/JAZZSNARE-05.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/SNARES/JAZZSNARE-06.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/SNARES/JAZZSNARE-07.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/SNARES/JAZZSNARE-08.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/SNARES/JAZZSNARE-09.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/SNARES/JAZZSNARE-10.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/SNARES/JAZZSNARE-11.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/SNARES/JAZZSNARE-12.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/SNARES/JAZZSNARE-13.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/SNARES/JAZZSNARE-14.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/SNARES/JAZZSNARE-15.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/SNARES/RIM-01.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/SNARES/RIM-02.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/SNARES/RIM-03.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/SNARES/RIM-04.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/SNARES/RIM-05.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/SNARES/RIM-06.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/SNARES/RIM-07.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/SNARES/RIM-08.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/SNARES/RIM-09.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/SNARES/RIM-10.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/SNARES/RIM-11.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/SNARES/RIM-12.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/SNARES/RIM-13.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/SNARES/RIM-14.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/SNARES/RIM-15.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/SNARES/RIM-16.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/SNARES/RIM-17.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/SNARES/SNARE+CRASH-1.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/SNARES/SNARE+CRASH-2.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/SNARES/SNARE+CRASH-3.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/SNARES/SNARE+CRASH-4.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/SNARES/SNARE+HAT-01.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/SNARES/SNARE+HAT-02.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/SNARES/SNARE-01.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/SNARES/SNARE-02.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/SNARES/SNARE-03.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/SNARES/SNARE-04.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/SNARES/SNARE-05.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/SNARES/SNARE-06.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/SNARES/SNARE-07.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/SNARES/SNARE-08.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/SNARES/SNARE-09.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/SNARES/SNARE-10.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/SNARES/SNARE-11.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/SNARES/SNARE-12.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/SNARES/SNARE-13.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/SNARES/SNARE-14.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/SNARES/SNARE-15.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/SNARES/SNARE-16.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/SNARES/SNARE-17.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/SNARES/SNARE-18.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/SNARES/SNARE-19.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/SNARES/SNARE-20.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/SNARES/SNARE-21.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/SNARES/SNARE-22.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/SNARES/SNARE-23.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/SNARES/SNARE-24.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/SNARES/SNARE-25.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/SNARES/SNARE-26.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/SNARES/SNARE-27.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/SNARES/SNARE-28.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/SNARES/SNARE-29.wav"
	];

	~samplers[\hh][\paths] = [
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/HATS/HAT-CL-51.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/HATS/HAT-CL-52.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/HATS/HAT-CL-53.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/HATS/HAT-CL-54.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/HATS/HAT-CL-55.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/HATS/HAT-CL-56.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/HATS/HAT-CL-57.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/HATS/HAT-CL-58.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/HATS/HAT-CL-59.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/HATS/HAT-CL-60.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/HATS/HAT-CL-61.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/HATS/HAT-CL-62.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/HATS/HAT-CL-63.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/HATS/HH-CL-01.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/HATS/HH-CL-02.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/HATS/HH-CL-03.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/HATS/HH-CL-04.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/HATS/HH-CL-05.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/HATS/HH-CL-06.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/HATS/HH-CL-07.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/HATS/HH-CL-08.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/HATS/HH-CL-09.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/HATS/HH-CL-10.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/HATS/HH-CL-11.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/HATS/HH-CL-12.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/HATS/HH-CL-13.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/HATS/HH-CL-14.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/HATS/HH-CL-15.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/HATS/HH-CL-16.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/HATS/HH-CL-17.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/HATS/HH-CL-18.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/HATS/HH-CL-19.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/HATS/HH-CL-20.wav",
		"/home/alik/win_f/sound/Drumdrops Drops In The Bronx/SINGLE HITS/HATS/HH-CL-21.wav",
	];




	// allocate buffers for every corresponding category
	~bufcounter = 1;
	~samplers.keysDo { |it i|
		var c = Condition.new(false);
		~samplers[it][\buffers] = ~samplers[it][\paths].collect { |path|
			var buffer;
			var file = SoundFile.openRead( path );
			var channels = if( file.numChannels < 2, { [0,0] },{ [0,1] });
			channels.postln;
			buffer = Buffer.readChannel(s, path, channels: channels, 
				action: {c.unhang},
				bufnum: s.options.numBuffers - ~bufcounter
			);
			c.hang;
			~bufcounter = ~bufcounter + 1;
			buffer
		};
	};

	// free all buffers
	// ~samplers.keysDo { |it i| ~samplers[it][\buffers].do { |buf| buf.free}};






} ).play;


)



( 
~samplers.keysDo({arg k; 
	"sample type:".postln;
	k.postln;
	~samplers[k].keysDo { |it|
		( "_______" ++ it ).postln
	};
	"\n".post
})
)





( // stereo

SynthDef(\simple_sampler, { | out, 
sample, rate=1|


	var samplePhasor, phasor, gran, env, bufrd; 
	var bufFrames = BufFrames.ir(sample); 

	samplePhasor = EnvGen.ar(Env(
		[ \offset.kr(0) * bufFrames, bufFrames - 1 ], 
		[ BufDur.kr(sample) * rate ], 
		curve: \rateCurve.kr(0)
	));
	
	env = EnvGen.ar(Env.adsr(
		\att.kr( 0.01 ), 
		\dec.kr( 0.01 ), 
		\sus.kr( 1.0 ), 
		\rel.kr( 0.01 ), 
		1.0, 
		\curve.kr( -4.0 )
	), 
		// don't use "gate" as control name here
		gate: \gt.kr(0), 
		doneAction: 2);

	
	bufrd = BufRd.ar(
		numChannels: 2, 
		bufnum: sample, 
		phase: samplePhasor, 
		interpolation: 4
	);
	
	bufrd = LeakDC.ar(bufrd);
	bufrd = bufrd * env;
	
	bufrd = Balance2.ar(bufrd[0], bufrd[1], \pan.kr(0));







	Out.ar(out, bufrd);

}).add;
)





// ~samplers.do { |it i| it[\ndefnames] = [\kk0, \kk1, \kk2];};


( 
~sz = 3;


{ 
~samplers.keysDo { |it j| 
	~samplers[it][\ndefnames] = List[];
	~sz.do { |i|
		var ndefname = ( it ++ "_" ++ i ).asSymbol;
		~samplers[it][\ndefnames].add(ndefname);

		Ndef(ndefname).ar(2);
		0.1.wait;
		Ndef(ndefname).play;
		0.1.wait;
		Ndef(ndefname).prime(\simple_sampler); // this will NOT start synth node
		// Ndef(ndefname, \simple_sampler); // this will start synth node
		0.1.wait;
		// Ndef(ndefname).put( 150, \filter -> { arg in;
		// 	var a;
		// 	a = LPF.ar(in, \lpf.kr( 18000 ));
		// 	a = HPF.ar(a, \hpf.kr( 20 ));
		// 	a = BLowShelf.ar(a,	 \lsF.kr( 200 ), \lsQ.kr(1.0), \lsdB.kr(0));
		// 	a = BHiShelf.ar(a,	 \hsF.kr( 80 ), \hsQ.kr(1.0), \hsdB.kr(0));
		// 	a = BPeakEQ.ar(a,	 \p0F.kr( 10000 ), \p0Q.kr(1.0), \p0dB.kr(0));
		// 	a = BPeakEQ.ar(a,	 \p1F.kr( 3000 ), \p1Q.kr(1.0), \p1dB.kr(0));
		// 	a = BPeakEQ.ar(a,	 \p2F.kr( 6000 ), \p2Q.kr(1.0), \p2dB.kr(0));
		// 	a
		// });
	};
}; 
}.fork




//////  specs

(

// sampler
Spec.add(\gt, \switch);
Spec.add(\offset, \unipolar);
Spec.add(\rate, [-2, 2]);
Spec.add(\att, [0.0, 0.1]);
Spec.add(\dec, [0.0, 0.1]);
Spec.add(\sus, [0.0, 1]);
Spec.add(\rel, [0.0, 4.1]);
Spec.add(\curve, [-8.0, 8.0]);
Spec.add(\rateCurve, [-8.0, 8.0]);


// eq
Spec.add(\lpf, \freq);
Spec.add(\hpf, \freq);
Spec.add(\hsF, \freq);
Spec.add(\lsF, \freq);
Spec.add(\p0F, \freq);
Spec.add(\p1F, \freq);
Spec.add(\p2F, \freq);
Spec.add(\hsQ, [0.0,2.0]);
Spec.add(\lsQ, [0.0,2.0]);
Spec.add(\p0Q, [0.0,2.0]);
Spec.add(\p1Q, [0.0,2.0]);
Spec.add(\p2Q, [0.0,2.0]);
Spec.add(\hsdB, [-20, 20.0]);
Spec.add(\lsdB, [-20, 20.0]);
Spec.add(\p0dB, [-20, 20.0]);
Spec.add(\p1dB, [-20, 20.0]);
Spec.add(\p2dB, [-20, 20.0]);

/// for nodeproxy dry-wets
Spec.add(\wet150, \unipolar);
Spec.add(\wet160, \unipolar);
Spec.add(\wet170, \unipolar);
Spec.add(\wet180, \unipolar);




///// add individual specs for buffer nums
 
~samplers.keysDo { |it|
	~samplers[it][\ndefnames].do { |ndefname i|
		Ndef(ndefname).addSpec(
			\sample, 
			[ 
				/// If I'd have access to slider value function slider
				/// would just provide index and function would use it
				/// to get bufnum from dictionary
				// 0, 
				// ~samplers[it][\buffers].size - 1,
				// Since I'm using manually allocated bufnums
				~samplers[it][\buffers].first.bufnum, 
				~samplers[it][\buffers].last.bufnum,
				\lin,
				1
			]
		);

		Ndef(ndefname).set(\sample, ~samplers[it][\buffers].first.bufnum + i)

	};

};
)

)


















( 
var midinotes;
var ndefnames = List[];
~samplers.do({|it| ndefnames.add( it[\ndefnames] ) });
ndefnames = ndefnames.flatten;
ndefnames.postln;
midinotes = (0..( ndefnames.size-1 ));

MIDIdef.noteOn(
	("sampler" ++"noteOn").asSymbol, 
	{ |vel, note, chan| 
		Ndef(ndefnames[note]).put(note, \simple_sampler, 0, [\gt, 1]);

		\on.postln;

	}, 
	noteNum: midinotes,
	chan: 4,
).fix; 


MIDIdef.noteOff(
	(\sampler ++ "noteOff").asSymbol, 
	{ |vel, note, chan| 
		Ndef(ndefnames[note]).objects[note].set( \gt, 0); 
		Ndef(ndefnames[note]).removeAt( note );
		\off.postln;
	}, 
	noteNum: midinotes,
	chan: 4
).fix;


)







m = NdefMixer(s,30)
ProxyMeter.addMixer(m);


( // 
Ndef('sn_1').set('sample', 324);
Ndef('kk_1').set('sample', 391);
Ndef('kk_0').set('sample', 398.0);
Ndef('perc_0').set('sample', 308);
Ndef('hh_2').set('sample', 428);
Ndef('perc_2').set('sample', 312.0);
Ndef('perc_1').set('sample', 309);
Ndef('hh_0').set('sample', 426);
Ndef('sn_2').set('sample', 325);
Ndef('kk_2').set('sample', 392);
Ndef('sn_0').set('sample', 333.0);
Ndef('hh_1').set('sample', 427);
)








