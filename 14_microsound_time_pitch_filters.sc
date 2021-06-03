// =====================================================================
// SuperCollider Workspace
// =====================================================================


b = Buffer.readChannel(s, "sound_file_path_here", channels:[0]);

b.play;
b.plot;
b.sampleRate;
b.numFrames;



( 
SynthDef(\brec, { arg b;
	var a, t;
	a = WhiteNoise.ar;
	RecordBuf.ar(
		a,
		bufnum: b,
		run: 1,
		loop:0,
		doneAction: 2
	);
}).play(args: [\b, b.bufnum]);
)





Ndef(\granulator).clear;
Ndef(\granulator).ar(2);
Ndef(\granulator).set(\bufnum, b.bufnum);




(
Ndef(\granulator, { |bufnum, tFreq=20, overlap=2, rate=1, posRate=1|
	var phasor, gran, env; 
	var bufFrames = BufFrames.ir(bufnum);
	var t = Impulse.ar(tFreq);

	phasor = Phasor.ar(
		rate: posRate * BufRateScale.kr(bufnum), 
		start: 0.0, 
		end: bufFrames, 
	);

	gran = GrainBuf.ar(
		numChannels: 1, 
		trigger: t, 
		dur: tFreq.reciprocal * overlap, 
		sndbuf: bufnum, 
		rate: rate, 
		pos: phasor / bufFrames, 
		// pos: Integrator.ar( K2A.ar(posRate) ) / BufFrames.ir(bufnum), // withoout phasor
		interp: 2, 
		pan: 0, 
		envbufnum: -1,  // Hann window
		maxGrains: 512, 
	);

	gran 
	* 
	0.3  !2

}).play;



Ndef(\granulator).addSpec(
	\overlap, [0.001, 40, \exp],
	\rate, [-1, 1, s.sampleRate.reciprocal],
	\posRate, [0, 1, s.sampleRate.reciprocal],
	// \rate, [0.9, 1, s.sampleRate.reciprocal],
	// \posRate, [0.9, 1, s.sampleRate.reciprocal],
	\tFreq, \widefreq,
	// \delay, [0.0, 30 / s.sampleRate, 1/ s.sampleRate]
	// \delay, [0.0, 0.1, 1/ s.sampleRate]
).edit;

)








( // stereo

// Ndef('granulator').set('tFreqMF', 3.5978835978836, 'tFreqMD', 14.074074074074, 'rate', 1.0042328042959, 'overlap', 10.664702036351, 'tFreq', 29.390328645693, 'bufnum', b.bufnum, 'posRate', 0.19576719702867);

Ndef(\granulator, { |bufnum, tFreq=20, overlap=2, rate=1, posRate=1, 
tFreqMF=0, tFreqMD=0, 
rateMF=0, rateMD=0, 
posRateMF=0, posRateMD=0|


	var phasor, gran, env; 
	var bufFrames = BufFrames.ir(bufnum); 
	var t;

	var tFreqMod = {
		SinOsc.ar(tFreqMF, Rand(0.0,2pi)) * tFreqMD;
	};

	var rateMod = {
		SinOsc.ar(rateMF, Rand(0.0,2pi)) * rateMD;
	};

	var posRateMod = {
		SinOsc.ar(posRateMF, Rand(0.0,2pi)) * posRateMD;
	};

	
	tFreq = tFreq + tFreqMod.dup;
	posRate = posRate + posRateMod.dup;
	rate = rate + rateMod.dup;
	

	t = Impulse.ar(tFreq);

	phasor = Phasor.ar(
		rate: posRate * BufRateScale.kr(bufnum), 
		start: 0.0, 
		end: bufFrames, 
	);

	gran = GrainBuf.ar(
		numChannels: 1, 
		trigger: t, 
		dur: tFreq.reciprocal * overlap, 
		sndbuf: bufnum, 
		rate: rate, 
		pos: phasor / bufFrames, 
		interp: 2, 
		pan: 0, 
		envbufnum: -1, 
		maxGrains: 512, 
	);

	gran  * 0.5

}).play;


Ndef(\granulator).addSpec(
	\overlap, [0.001, 40, \exp],
	\rate, [-2, 2],
	\posRate, [0, 4],
	\tFreq, \widefreq,
	\tFreqMF, [0.0,20],
	\tFreqMD, [0.0,20],
	\rateMF, [0.0,20],
	\rateMD, [0.0,2],
	\posRateMF, [0.0,20],
	\posRateMD, [0.0,2],
).edit;




)






(
SynthDef(\granulator, { |bufnum, tFreq=20, overlap=2, rate=1, posRate=1, 
	tFreqMF=0, tFreqMD=0, 
	rateMF=0, rateMD=0, 
	posRateMF=0, posRateMD=0|


	var phasor, gran, env; 
	var bufFrames = BufFrames.ir(bufnum); 
	var t;

	var tFreqMod = {
		SinOsc.ar(tFreqMF, Rand(0.0,2pi)) * tFreqMD;
	};

	var rateMod = {
		SinOsc.ar(rateMF, Rand(0.0,2pi)) * rateMD;
	};

	var posRateMod = {
		SinOsc.ar(posRateMF, Rand(0.0,2pi)) * posRateMD;
	};

	
	tFreq = tFreq + tFreqMod.dup;
	posRate = posRate + posRateMod.dup;
	rate = rate + rateMod.dup;
	

	t = Impulse.ar(tFreq.lag(0.05));

	phasor = Phasor.ar(
		trig: 0.0, 
		rate: posRate * BufRateScale.kr(bufnum), 
		start: 0.0, 
		end: bufFrames, 
		resetPos: 0.0
	);

	gran = GrainBuf.ar(
		numChannels: 1, 
		trigger: t, 
		dur: tFreq.reciprocal * overlap, 
		sndbuf: bufnum, 
		rate: rate, 
		pos: phasor / bufFrames, 
		interp: 2, 
		pan: 0, 
		envbufnum: -1, 
		maxGrains: 512, 
	);

	Out.ar(0, gran  * \amp.kr(0.3))

}).add;
)







b = Buffer.readChannel(s, "sound_file_path_here", channels:[0]);
b.play;


( 
r = Routine({
	x = Synth(\granulator, [\amp, 0.3, 'tFreqMF', 3.5978835978836, 'tFreqMD', 14.074074074074, 'rate', 1.0042328042959, 'overlap', 10.664702036351, 'tFreq', 29.390328645693, 'bufnum', b.bufnum, 'posRate', 0.19576719702867]);

	20.wait;
	"enough waiting".postln;

	10.do { |i|
		x.set(
			'rate', 1,
			'overlap', 12, 
			'tFreq', exprand(2,40.0), 
			'tFreqMD', exprand(2,5.0), 
			'tFreqMF', exprand(1,4.0), 
			'posRate', exprand(0.01,0.4), 
			\rateMD, 0.001, 
			\rateMF, 0.1,
			\posRateMD, 0.4, 
			\posRateMF, 3
);

		i.postln;

		( 2.pow(rrand(-2,2)) * 10 ).wait;
	}
});

r.randSeed = 1234;
r.play

)


