( 
Spec.add(\freq, \widefreq);
Spec.add(\mInd, [0.0,10]);
Spec.add(\amp_0, [0.0,1]);
Spec.add(\amp_1, [0.0,1]);
Spec.add(\amp_2, [0.0,1]);
Spec.add(\amp_3, [0.0,1]);
Spec.add(\amp_4, [0.0,1]);
Spec.add(\fmnt_0, [1,20, \lin, 1,1]);
Spec.add(\fmnt_1, [1,20, \lin, 1,1]);
Spec.add(\fmnt_2, [1,20, \lin, 1,1]);
Spec.add(\fmnt_3, [1,20, \lin, 1,1]);
Spec.add(\fmnt_4, [1,20, \lin, 1,1]);
Spec.add(\wdt_0, \freq);
Spec.add(\wdt_1, \freq);
Spec.add(\wdt_2, \freq);
Spec.add(\wdt_3, \freq);
Spec.add(\wdt_4, \freq);


)



Ndef(\fm_1mNc).clear;
Ndef(\fm_1mNc).gui;

( 
Spec.add(\fmnt_0, [0,5]);
Spec.add(\fmnt_1, [0,5]);
Spec.add(\mfreq, \freq);
Spec.add(\cfreq, \freq);
Spec.add(\stretch, [1,10, \lin, 1, 1]);

Ndef(\fm_1mNc,{ 
	var fr_0,  mfr, cfr, mod, fm;
	e = EnvGen.ar(Env.perc(0.01,3), gate: Impulse.ar(0.4));
	
	cfr	= \cfreq.kr(100);
	mfr = \mfreq.kr(0); // freq step
	
	mod = SinOsc.ar(mfr * \stretch.kr(3));

	fr_0 = cfr + ( mod * ( \wdt_0.kr(50) ) );


	fm = SinOsc.ar(fr_0) * \amp_0.kr(0.2) !2;
	fm = SinOsc.ar(mfr) * 0.5 + fm;	
	fm =  fm * e.pow(0.9);

	// HPF.ar( fm  * 0.07, mfr * 0.5 );
}).play
)



( // mod freq as fundamental

Ndef(\fm_1mNc,{ 
	var fr_0, fr_1, fr_2, mfr, mod, fm;
	e = EnvGen.ar(Env.perc(0.01,3), gate: Impulse.ar(0.4));
	
	mfr		= \freq.kr(100);
	// mfr	= LFNoise0.kr(1).exprange(50,333);
	
	mod = SinOsc.ar(mfr *\stretch.kr(3));

	fr_0 = mfr*\fmnt_0.kr(1) + ( mod * ( \wdt_0.kr(50) ) );
	fr_1 = mfr*\fmnt_1.kr(2) + ( mod * ( \wdt_1.kr(50) ) );


	fm = Mix([
		SinOsc.ar(fr_0) * e.pow(1.1) * \amp_0.kr(0.92),
		SinOsc.ar(fr_1) * e.pow(0.7) * \amp_1.kr(0.5),
	]);



	HPF.ar( fm  * 0.07, mfr * 0.5 )!2;
}).play
)





Ndef(\fm_1mNc).clear;
Ndef(\fm_1mNc).gui;
( 
Ndef(\fm_1mNc,{ 
	var fr_0, fr_1, fr_2,fr_3,fr_4, mfr, mod, fm;
	var f_rng = [50,1600];
	var frek, amp, ring, klank;
	e = EnvGen.ar(Env.perc(0.01,3), gate: Impulse.ar(0.4));
	// e = e * LFNoise2.kr(12!2).range(0.4,1);

	mfr		= \freq.kr(111);
	// mfr	= LFNoise0.kr(1).exprange(50,333);
	mfr = mfr.clip(f_rng[0],f_rng[1]);
	
	mod = SinOsc.ar(
		mfr * \stretch.kr(3),// + {Rand(-1,3)}.dup, 
		// PinkNoise.ar(2pi * mfr.linlin(50,1600,0.8,0.2)!2).lag(0.06)
	) * e.pow(1.3) 
	// * LFTri.kr(e * 7).range(0.4,3)
	;

	fr_0 = mfr*\fmnt_0.kr(1) + ( mod * ( \wdt_0.kr(550) ) );
	fr_1 = mfr*\fmnt_1.kr(2) + ( mod * ( \wdt_1.kr(330) ) );
	fr_2 = mfr*\fmnt_2.kr(3) + ( mod * ( \wdt_2.kr(110) ) );
	fr_3 = mfr*\fmnt_3.kr(7) + ( mod * ( \wdt_3.kr(220) ) );
	fr_4 = mfr*\fmnt_4.kr(8) + ( mod * ( \wdt_4.kr(150) ) );

    // fr_0 = fr_0	+ SinOsc.ar(mfr * 0.5, Rand(0,2pi), mfr * 0.4 * e);
    // fr_1 = fr_1	+ SinOsc.ar(3,         Rand(0,2pi), mfr * 0.01 * e);
    // fr_2 = fr_2	+ SinOsc.ar(mfr * 0.75,Rand(0,2pi), mfr * 0.05 * e);
    // fr_3 = fr_3	+ SinOsc.ar(4,         Rand(0,2pi), mfr * 0.1 * e);
    // fr_4 = fr_4	+ SinOsc.ar(mfr * 0.5, Rand(0,2pi), mfr * 0.15 * e);


	fm = Mix([
		SinOsc.ar(fr_0) * e.pow(1.1) * \amp_0.kr(0.92),
		SinOsc.ar(fr_1) * e.pow(0.7) * \amp_1.kr(0.5),
		SinOsc.ar(fr_2) * e.pow(0.8) * \amp_2.kr(0.5),
		SinOsc.ar(fr_3) * e.pow(mfr.linlin( f_rng[0], f_rng[1], 1, 3 )) * \amp_3.kr(0.1),
		SinOsc.ar(fr_4) * e.pow(mfr.linlin( f_rng[0], f_rng[1], 1, 3.5 )) * \amp_4.kr(0.2),
	]);



	HPF.ar( fm  * 0.07, mfr * 0.5 )!2;
}).play
)





Ndef(\fm_1mNc).clear;
(
Ndef(\fm_1mNc,{ 
	var fr_0, fr_1, fr_2,fr_3,fr_4, mfr, mod, fm;
	var f_rng = [50,1600];
	var frek, amp, ring, klank;
	e = EnvGen.ar(Env.perc(0.01,3), gate: Impulse.ar(0.4));
	e = e * LFNoise2.kr(12!2).range(0.4,1);
	mfr		= \freq.kr(111);
	// mfr	= LFNoise0.kr(1).exprange(50,333);
	mfr = mfr.clip(f_rng[0],f_rng[1]);
	
	mod = SinOsc.ar(
		mfr * \stretch.kr(3) + {Rand(-1,3)}.dup, 
		// PinkNoise.ar(2pi * mfr.linlin(50,1600,0.8,0.2)!2).lag(0.06)
	) * e.pow(1.3) 
	// * LFTri.kr(e * 7).range(0.4,3)
	;

	fr_0 = mfr*\fmnt_0.kr(1) + ( mod * ( \wdt_0.kr(550) ) );
	fr_1 = mfr*\fmnt_1.kr(2) + ( mod * ( \wdt_1.kr(330) ) );
	fr_2 = mfr*\fmnt_2.kr(3) + ( mod * ( \wdt_2.kr(110) ) );
	fr_3 = mfr*\fmnt_3.kr(7) + ( mod * ( \wdt_3.kr(220) ) );
	fr_4 = mfr*\fmnt_4.kr(8) + ( mod * ( \wdt_4.kr(150) ) );

    fr_0 = fr_0	+ SinOsc.ar(mfr * 0.5, Rand(0,2pi), mfr * 0.4 * e);
    fr_1 = fr_1	+ SinOsc.ar(3,         Rand(0,2pi), mfr * 0.01 * e);
    fr_2 = fr_2	+ SinOsc.ar(mfr * 0.75,Rand(0,2pi), mfr * 0.05 * e);
    fr_3 = fr_3	+ SinOsc.ar(4,         Rand(0,2pi), mfr * 0.1 * e);
    fr_4 = fr_4	+ SinOsc.ar(mfr * 0.5, Rand(0,2pi), mfr * 0.15 * e);


	fm = Mix([
		SinOsc.ar(fr_0) * e.pow(1.1) * \amp_0.kr(0.92),
		SinOsc.ar(fr_1) * e.pow(0.7) * \amp_1.kr(0.5),
		SinOsc.ar(fr_2) * e.pow(0.8) * \amp_2.kr(0.5),
		SinOsc.ar(fr_3) * e.pow(mfr.linlin( f_rng[0], f_rng[1], 1, 3 )) * \amp_3.kr(0.1),
		SinOsc.ar(fr_4) * e.pow(mfr.linlin( f_rng[0], f_rng[1], 1, 3.5 )) * \amp_4.kr(0.2),
	]);



	
	// freqs
	frek = \frek.ir(Array.series(4, 120, 520).postln);
	// amplitudes
	amp = \amp.ir(Array.fill(4, {arg i; 0.9 ** (i+1 * 8)}));
	// ring times
	ring = \ring.ir([0.01, 0.01,0.11,0.01]*2);

	klank = Klank.ar(`[frek, amp, ring], fm);	
	// klank = Klank.ar(`[frek, amp, ring], WhiteNoise.ar(0.5!2));	

	fm = Mix([
		// fm ,
		klank * 0.5
	]);


	HPF.ar( fm  * 0.03, mfr * 0.5 );
}).play
)




( 
SynthDef(\fm_1mNc,{ | gate=1 |
	var fr_0, fr_1, fr_2,fr_3,fr_4, mfr, mod, fm;
	var f_rng = [50,1600];
	var frek, amp, ring, klank;
	e = EnvGen.ar(Env.adsr(0.01, 0.01, 0.4,3), gate: gate, doneAction: 2);
	e = e * LFNoise2.kr(30!2).range( 1 - ( e * 0.5 ),1);
	// e = e * LFNoise2.kr(12!2).range(0.4,1);
	e = e * \vel.kr(1);

	mfr		= \freq.kr(111);
	mfr = mfr.clip(f_rng[0],f_rng[1]);
	
	mod = SinOsc.ar(
		mfr * \stretch.kr(3) + {Rand(-1,3)}.dup, 
		PinkNoise.ar(2pi * mfr.linlin(50,1600,0.1,1) !2)
	) * e.pow(1.2) 
	// * LFTri.kr(e * 7).range(0.4,3)
	;

	fr_0 = mfr*\fmnt_0.kr(1) + ( mod * ( \wdt_0.kr(550) ) );
	fr_1 = mfr*\fmnt_1.kr(2) + ( mod * ( \wdt_1.kr(330) ) );
	fr_2 = mfr*\fmnt_2.kr(3) + ( mod * ( \wdt_2.kr(110) ) );
	fr_3 = mfr*\fmnt_3.kr(7) + ( mod * ( \wdt_3.kr(220) ) );
	fr_4 = mfr*\fmnt_4.kr(8) + ( mod * ( \wdt_4.kr(150) ) );

    fr_0 = fr_0	+ SinOsc.ar(mfr * 0.5, Rand(0,2pi), mfr * 0.4 * e);
    fr_1 = fr_1	+ SinOsc.ar(3,         Rand(0,2pi), mfr * 0.01 * e);
    fr_2 = fr_2	+ SinOsc.ar(mfr * 0.75,Rand(0,2pi), mfr * 0.05 * e);
    fr_3 = fr_3	+ SinOsc.ar(4,         Rand(0,2pi), mfr * 0.1 * e);
    fr_4 = fr_4	+ SinOsc.ar(mfr * 0.5, Rand(0,2pi), mfr * 0.15 * e);


	fm = Mix([
		SinOsc.ar(fr_0) * e.pow(0.5) * \amp_0.kr(1),
		SinOsc.ar(fr_1) * e.pow(0.7) * \amp_1.kr(0.5),
		SinOsc.ar(fr_2) * e.pow(0.8) * \amp_2.kr(0.5),
		SinOsc.ar(fr_3) * e.pow(mfr.linlin( f_rng[0], f_rng[1], 1, 3 )) * \amp_3.kr(0.1),
		SinOsc.ar(fr_4) * e.pow(mfr.linlin( f_rng[0], f_rng[1], 1, 3.5 )) * \amp_4.kr(0.2),
	]);

	
	// freqs
	frek = \frek.ir(Array.series(4, 100, 555).postln);
	// amplitudes
	amp = \amp.ir(Array.fill(4, {arg i; 0.9 ** (i+1 * 8)}));
	// ring times
	ring = \ring.ir([0.05, 0.03,0.01,0.01]*2);

	klank = Klank.ar(`[frek, amp, ring], fm);	

	fm = Mix([
		fm ,
		klank * 0.3
	]);



	Out.ar(0, 	HPF.ar( fm  * 0.05, mfr * 0.5 ));
}).add
)

( 
Pbind(
	\instrument, \fm_1mNc,
	\dur, Pseq( 2.pow((-3..1).scramble), inf ),
	\legato, Pfunc({rrand(0.01,0.1)}),
	\degree, Pseq([0,2,5,6,8,11].scramble, inf),
	\octave, Pstutter(3, Pseq([3,4,5], inf) ),
	\vel, Pstutter(6, Pseq([ 0.2, 1, 1.5 ], inf)),
).play; 
)

