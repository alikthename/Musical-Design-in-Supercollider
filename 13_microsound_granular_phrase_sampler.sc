// =====================================================================
// SuperCollider Workspace
// =====================================================================


b = Buffer.readChannel(s, "/mnt/4/sound/gitarka_2/78.wav", channels:[0]);
b.play;
b.sampleRate;
b.numFrames;



Ndef(\granular_reconstruct).clear;
Ndef(\granular_reconstruct).ar(2);
Ndef(\granular_reconstruct).set(\bufnum, b.bufnum);


(
Ndef(\granular_reconstruct, { |bufnum, overlap=2, tFreq=20|
	var phasor, bufrd, gran, env; 
	var bufFrames = BufFrames.ir(bufnum);
	var t = Impulse.ar(tFreq);

	phasor = Phasor.ar(
		rate: 1.0, 
		start: 0.0, 
		end: bufFrames, 
	);

	bufrd = BufRd.ar(
		numChannels: 1, 
		bufnum: bufnum, 
		phase: phasor, 
		interpolation: 0
	);

	gran = GrainBuf.ar(
		numChannels: 1, 
		trigger: t, 
		dur: overlap / tFreq, 
		sndbuf: bufnum, 
		rate: 1, 
		pos: phasor / bufFrames, 
		interp: 0, 
		pan: 0, 
		envbufnum: -1, 
		maxGrains: 512, 
	);


	// bufrd
	// - 
	gran
	!2
}).play;
)

( 
Ndef(\granular_reconstruct).addSpec(
	\overlap, [0.0, 4],
	\tFreq, \widefreq
).edit;
)








// env
e = Buffer.sendCollection(s, Signal.hanningWindow(1024));
// e = Buffer.sendCollection(s, Signal.welchWindow(1024));


e.plot

( // playing it at right rate to fit duration

var dur=0.4;
{
	PlayBuf.ar(1, e.bufnum, rate: 1 / ( dur * s.sampleRate / e.numFrames ), doneAction: 2);
}.plot(dur)

)


(
SynthDef(\grain, { |out=0, bufnum, envbufnum, dur=0.1, pos=0|
	var phasor, bufrd, env;
	var bufFrames = BufFrames.ir(bufnum);

    phasor = Phasor.ar(
		rate: 1.0, 
		start: pos, 
		end: bufFrames, 
	);

	bufrd = BufRd.ar(
		numChannels: 1, 
		bufnum: bufnum, 
		phase: phasor, 
		interpolation: 0
	);
	
	env = PlayBuf.ar(1, envbufnum, rate: ( dur / BufDur.ir(envbufnum) ).reciprocal, doneAction: 2);

	bufrd = env * bufrd * 0.4;
	// bufrd = env;

    // Out.ar(out, bufrd!2)
    OffsetOut.ar(out, bufrd!2)
}).add;
)


Synth.grain(\grain, [\bufnum, b.bufnum, \envbufnum, e.bufnum, \dur, 1.2, \pos, 111111]);



( 
// s.latency = 0.15;
s.latency = 1024 * 6 / 44100;
Tdef(\grains,  {
	// var interonset = 1024 / 44100; // sec
	var interonset = 0.001; // sec
	inf.do { arg i;

		s.sendBundle( s.latency, 
			[
				's_new', \grain, 
				-1,				// nodeID
				1,				// addAction
				1,				// targetID
				// synth args
				\bufnum, b.bufnum,
				\envbufnum, e.bufnum,
				\dur, interonset * 2,
				\pos, interonset * s.sampleRate * i % b.numFrames
			]
		);

		interonset.wait;
	}
} ).play;
)



( //envelopes
SynthDef(\grain, { |out=0, bufnum, dur=0.1, pos=0|
	var phasor, bufrd, env;
	var bufFrames = BufFrames.ir(bufnum);

    phasor = Phasor.ar(
		trig: 0.0, 
		rate: 1.0, 
		start: pos, 
		end: bufFrames, 
		resetPos: 0.0
	);

	bufrd = BufRd.ar(
		numChannels: 1, 
		bufnum: bufnum, 
		phase: phasor, 
		interpolation: 0
	);
	
	env = Env.linen(
		attackTime: dur * 0.3, 
		sustainTime: dur * 0.4, 
		releaseTime: dur * 0.3, 
		level: 1.0, 
		// curve: \welch,
		// curve: \sine,
		// curve: -9,
		// curve: 9
		// curve: [-5,5,0]
	);

	// env = Env.sine(dur);
	// env = Env.perc(dur * 0.01, dur * 0.99);

	bufrd = EnvGen.ar( env, doneAction: 2 ) * bufrd * 0.3;

    // Out.ar(out, bufrd!2)
    OffsetOut.ar(out, bufrd!2)
}).add;
)












// periodic tone

({
Out.ar(0, Impulse.ar(800).lag(0.005) !2);
}.play )

( 
s.latency = 0.15;
r = Routine {
	var interonset = 800.reciprocal;
	loop {
		s.sendBundle(s.latency, [
		// s.sendMsg(
			\s_new, \grain, 
			s.nextNodeID,	// nodeID
			1,				// targetID
			1,				// addAction
			// synth args
			\bufnum, b.bufnum,
			\dur, interonset * 0.5,
			\pos, 20000
		]
);
		interonset.wait;
	}
};

SystemClock.play(r)
)


( // another approach (same result)
f = { 
	s.sendBundle(s.latency, [
		// s.sendMsg(
		\s_new, \grain, 
		s.nextNodeID,	// nodeID
		1,				// targetID
		1,				// addAction
		// synth args
		\bufnum, b.bufnum,
		\dur, 200.reciprocal * 0.5,
		\pos, 20000
	]
	);
}; 

g = {arg i; SystemClock.sched(200.reciprocal * i, f)};

for(0, 1000, g);
)

// on topic from James McCartney: https://medias.ircam.fr/xb090dd_supercollider-and-time

// use NRT to get precise timings and low interonset times



