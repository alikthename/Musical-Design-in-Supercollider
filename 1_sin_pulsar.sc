
( // basic
SynthDef(\sin_pulsar, { 
	var a,b,c,d,e,f;
	var overlap;

	overlap	= MouseY.kr(0,0.4);

	f = MouseX.kr(1,20);

	a = GrainSin.ar(
		numChannels: 1, 
		trigger: Impulse.ar(freq: f), 
		dur: overlap, 
		freq: 440
	); 

	a = LeakDC.ar(a);

	Out.ar(0, a * 0.1!2)
}).play;
// }).draw;
)


( // bind overlap to f
SynthDef(\sin_pulsar, { 
	var a,b,c,d,e,f;
	var overlap;

	overlap = MouseY.kr(0,2).poll;

	f = MouseX.kr(1,220);

	a = GrainSin.ar(
		numChannels: 1, 
		trigger: Impulse.ar(f), 
		dur: overlap/f, 
		freq: 440
	); 

	a = LeakDC.ar(a);

	Out.ar(0, a * 0.1!2);
}).play;
)




( // bind sin freq to f
SynthDef(\sin_pulsar, { 
	var a,b,c,d,e,f;
	var overlap = MouseY.kr(0,2);
	f = 220;
	// f = LFNoise0.kr(1).exprange(100,200);

	a = GrainSin.ar(
		numChannels: 1, 
		trigger: Impulse.ar(f), 
		dur: overlap/f, 
		freq: MouseX.kr(1,16) * f
	); 
	
	a = LeakDC.ar(a);

	Out.ar(0, a * 0.1!2);
}).play;
)


( // add fundamental
SynthDef(\sin_pulsar, { 
	var a,b,c,d,e,f, fund;
	var overlap = MouseY.kr(0,2);
	f = 220;

	fund = GrainSin.ar(
		numChannels: 1, 
		trigger: Impulse.ar(f), 
		dur: 1/f,
		freq: f
	);

	a = GrainSin.ar(
		numChannels: 1, 
		trigger: Impulse.ar(f), 
		dur: overlap/f, 
		freq: MouseX.kr(1,16) * f
	); 

	c = Mix([
		fund,
		a
	]);
	
	c = LeakDC.ar(c);

	Out.ar(0, c * 0.2!2);
}).play;
)


( // add envelope generator

SynthDef(\sin_pulsar, { 
	var a,b,c,d,e,f,fund;


	var overlapA = LFNoise1.kr(0.3).range(0.1,2);
	var overlapB = LFNoise1.kr(0.3).range(0.1,2);

	e = EnvGen.kr(
		Env.perc( 
			attackTime: Rand(0.01,0.04),
			releaseTime: Rand(1,2) 
		), 
		gate: \gate.kr(1), doneAction: 2
	);

	f = \freq.kr(220);


	fund = GrainSin.ar(
		numChannels: 1, 
		trigger: Impulse.ar(f), 
		dur: 1/f,
		freq: f
	);

	a = GrainSin.ar(
		numChannels: 1, 
		trigger: Impulse.ar(f), 
		dur: overlapA / f, 
		freq: Rand(1,4) * f
	); 

	b = GrainSin.ar(
		numChannels: 1, 
		trigger: Impulse.ar(f), 
		dur: overlapB / f, 
		freq: Rand(5,13) * f
	);


	c = Mix([
		fund,
		a,
		b,
	]) * e;


	c = LeakDC.ar(c);

	Out.ar(0, c * 0.2!2)

}).add;

)




( 
Pbind(
	\instrument, \sin_pulsar,
	\dur, Pseq([ 0.1,0.5,1, 2 ], inf),
	\degree, Pseq([ 1,3,5,7, 9, 11 ], inf),
	\octave, Pstutter(3, Pseq([3,4,5], inf) ),

).play; 
)








( // naturalize

SynthDef(\sin_pulsar, { |out=0|
	var a,b,c,d,e,f, f_, fund ,t,lev, formantA, formantB, overlapA, overlapB;
	lev = \lev.kr(0.3);	
	f = \freq.kr(440);
	f_ = f.clip(50, 1500);

	formantA = \formantA.kr(2);
	formantB = \formantB.kr(6);
	overlapA = \overlapA.ar(2);
	overlapB = \overlapB.kr(2);

	e = EnvGen.kr(
		Env.perc( 
			attackTime: ( f_.linlin( 50, 1500, 0.1, 0.02) ),
			releaseTime: Rand(1,7) 
		), 
		gate: \gate.kr(1)
	);
	
	e = e * PinkNoise.ar(1!2).range( 0.1, 1 ).lag(0.02) ;


	fund = GrainSin.ar(
		numChannels: 1, 
		trigger: Impulse.ar(f), 
		dur:  overlapA * ( 1.05 - e.pow(1.1) )
		// dur: Rand(0.5,2)  * ( 0.05 + e.pow(0.5) ) 
		/ f,
		freq: f,
	);

	a = GrainSin.ar(
		numChannels: 1, 
		trigger: Impulse.ar(f), 
		dur:  overlapA * ( 1.05 - e.pow(1.5) )
		// dur:  overlapA * ( 0.05 + e.pow(0.5) ) 
		/ f, 
		freq: f * formantA,
	);


	b = GrainSin.ar(
		numChannels: 1, 
		trigger: Impulse.ar(f), 
		dur:  overlapB * ( 1.05 - e.pow(2.5) ) 
		// dur:  overlapB * ( 0.05 + e.pow(0.5) ) 
		/ f,
		freq: f * formantB,
	);



	c = Mix([
		fund * ( lev * e ).pow(0.7),
		a * ( lev * e ).pow(f_.linlin( 50, 1500, 1.1, 1.5)),
		b * ( lev * e ).pow(f_.linlin( 50, 1500, 1.5, 2.5)),
	]);


	d = LeakDC.ar(c);


	
	DetectSilence.ar(d, doneAction: 2);
	Out.ar(out, d )
}).add; 

)



( 
Pbind(
	\instrument, \sin_pulsar,
	\dur, Pseq([ 0.1,0.5,1, 2 ], inf),
	\degree, Pseq([ 1,3,5,7, 9, 11 ], inf),
	\octave, Pstutter(3, Pseq([3,4,5], inf) ),

	\overlapA, Pseq(2.pow([-1,0,1,2]), inf),
	\overlapB, Pseq(2.pow([-1,0,1].scramble), inf),

	\formantA, Pseq([1,2,3], inf),
	\formantB, Pseq([3,4,5,6,7].scramble, inf),
	\lev, Pshuf(( 1..10 ) * 0.01, inf)
).play; 
)




({ // noizy envelope
e = EnvGen.ar(Env.perc(0.1, 0.8), doneAction: 2);
e = e * PinkNoise.ar(1!2).range(0.3,1).lag(0.05);
}.plot(1))



({ // .pow applied to envelope
var e = EnvGen.kr(Env.perc(0.01, 0.02), doneAction: 2);
[
	e,
	e.pow(0.1),
	e.pow(3)
]
}.plot(0.03))















( // mods

SynthDef(\sin_pulsar, { |out=0|
	var a,b,c,d,e,f, f_, fund ,t,lev, formantA, formantB, overlapA, overlapB;
	lev = \lev.kr(0.3);	
	f = \freq.kr(440);
	f_ = f.clip(50, 1500);

	formantA = \formantA.kr(2);
	formantB = \formantB.kr(6);
	overlapA = \overlapA.ar(2);
	overlapB = \overlapB.kr(2);

	e = EnvGen.kr(
		Env.perc( 
			attackTime: ( f_.linlin( 50, 1500, 0.1, 0.02) ),
			releaseTime: Rand(1,7) 
		), 
		gate: \gate.kr(1)
	);
	
	e = e * PinkNoise.ar(1!2).range( 0.1, 1 ).lag(0.02) ;


	fund = GrainSin.ar(
		numChannels: 1, 
		trigger: Impulse.ar(f), 
		dur:  Rand(0.5, 2) * ( 1.05 - e.pow(1.1) )
		// dur: Rand(0.5,2)  * ( 0.05 + e.pow(1.1) ) 
		*SinOsc.ar(e * 6, {Rand(0, 6.28)}.dup).range(0.87,1.15)  
		/ f,
		freq: f 
		* SinOsc.ar(e * 8, {Rand(0, 6.28)}.dup).range(0.9,Rand(1,1.5))
		,
	);

	a = GrainSin.ar(
		numChannels: 1, 
		trigger: Impulse.ar(f + Rand(-3,3)), 
		dur:  overlapA * ( 1.05 - e.pow(1.5) )
		// dur:  overlapA * ( 0.05 + e.pow(1.5) )
		* SinOsc.ar(e * 6, {Rand(0, 6.28)}.dup).range(0.87,1.15) 
		/ f, 
		freq: f * 
		( formantA  * SinOsc.ar(7 * e, {Rand(0, 6.28)}.dup).range(0.9,1.1) ),
	);


	b = GrainSin.ar(
		numChannels: 1, 
		trigger: Impulse.ar(f + Rand(-3,3)), 
		dur:  overlapB * ( 1.05 - e.pow(2.5) ) 
		// dur:  overlapB * ( 0.05 + e.pow(2.5) ) 
		*SinOsc.ar(e * 6, {Rand(0, 6.28)}.dup).range(0.87,1.15)  
		/ f,
		freq: f * 
		( formantB * SinOsc.ar(e * 8, {Rand(0, 6.28)}.dup).range(0.9,Rand(1,1.5)) ),
	);


	c = Mix([
		fund * ( lev * e ).pow(0.7),
		a * ( lev * e ).pow(f_.linlin( 50, 1500, 1.1, 1.5)),
		b * ( lev * e ).pow(f_.linlin( 50, 1500, 1.5, 2.5)),
	]);


	d = LeakDC.ar(c);


	
	DetectSilence.ar(d, doneAction: 2);
	Out.ar(out, d )
}).add; 

)

( 
Pbind(
	\instrument, \sin_pulsar,
	\dur, Pseq([ 0.1,0.5,1, 2 ], inf),
	// \dur, 0.3,
	// \delta, 2,
	\degree, Pseq([ 1,3,5,7, 9, 11 ], inf),
	// \degree, 1,
	\octave, Pstutter(3, Pseq([3,4,5], inf) ),

	\overlapA, Pseq(2.pow([-1,0,1,2]), inf),
	\overlapB, Pseq(2.pow([-1,0,1].scramble), inf),

	\formantA, Pseq([1,2,3], inf),
	// \formantA, 4,
	\formantB, Pseq([3,4,5,6,7].scramble, inf),
	// \formantB, 16,
	\lev, Pshuf(( 1..10 ) * 0.01, inf)
	// \lev, 0.3
).play; 
)















( // space

SynthDef(\sin_pulsar, { |out=0|
	var a,b,c,d,e,f, f_ ,fund ,t,lev, formantA, formantB, overlapA, overlapB;
	lev = \lev.kr(0.3);	
	f = \freq.kr(440);
	f_ = f.clip(50, 1500);

	formantA = \formantA.kr(2);
	formantB = \formantB.kr(6);
	overlapA = \overlapA.ar(2);
	overlapB = \overlapB.kr(2);

	e = EnvGen.kr(
		Env.perc( 
			attackTime: ( f_.linlin( 50, 1500, 0.1, 0.02) ),
			releaseTime: Rand(1,7) 
		), 
		gate: \gate.kr(1)
	);
	
	e = e * PinkNoise.ar(1!2).range( 0.1, 1 ).lag(0.02) ;


	fund = GrainSin.ar(
		numChannels: 1, 
		trigger: Impulse.ar(f), 
		dur:  Rand(0.5, 2) * ( 1.05 - e.pow(1.1) )
		// dur: Rand(0.5,2)  * ( 0.05 + e.pow(1.1) ) 
		*SinOsc.ar(e * 3, {Rand(0, 6.28)}.dup).range(0.87,1.15)  
		/ f,
		freq: f 
		* SinOsc.ar(e * 3, {Rand(0, 6.28)}.dup).range(0.9,Rand(1,1.5))
		,
		pan: PinkNoise.ar(0.5!2), 
	);

	a = GrainSin.ar(
		numChannels: 1, 
		trigger: Impulse.ar(f + Rand(-3,3)), 
		dur:  overlapA * ( 1.05 - e.pow(1.5) )
		// dur:  overlapA * ( 0.05 + e.pow(1.5) )
		* SinOsc.ar(e * 4, {Rand(0, 6.28)}.dup).range(0.87,1.15) 
		/ f, 
		freq: f * 
		( formantA  * SinOsc.ar(4 * e, {Rand(0, 6.28)}.dup).range(0.9,1.1) ),
		pan: PinkNoise.ar(0.5!2), 
	);


	b = GrainSin.ar(
		numChannels: 1, 
		trigger: Impulse.ar(f + Rand(-3,3)), 
		dur:  overlapB * ( 1.05 - e.pow(2.5) ) 
		// dur:  overlapB * ( 0.05 + e.pow(2.5) ) 
		*SinOsc.ar(e * 6, {Rand(0, 6.28)}.dup).range(0.87,1.15)  
		/ f,
		freq: f * 
		( formantB * SinOsc.ar(e * 5, {Rand(0, 6.28)}.dup).range(0.9,Rand(1,1.5)) ),
		pan: PinkNoise.ar(0.5!2), 
	);

	c = Mix([
		fund * ( lev * e ).pow(0.7),
		a * ( lev * e ).pow(f_.linlin( 50, 1500, 1.1, 1.5)),
		b * ( lev * e ).pow(f_.linlin( 50, 1500, 1.5, 2.5)),
	]);


	c = LeakDC.ar(c);

	d = c;	

	4.do { |i|
		d = AllpassC.ar(
			d,
			delaytime: LFNoise1.kr(0.2!2).range(0.01,0.02), 
		)
	} * 0.5;	

	d = c * Rand(0.1,0.9) + d; 	
	
	DetectSilence.ar(d, doneAction: 2);
	Out.ar(out, d )
}).add; 

)

( 
Pbind(
	\instrument, \sin_pulsar,
	\dur, Pseq([ 0.1,0.5,1, 2 ], inf),
	\degree, Pseq([ 1,3,5,7, 9, 11 ], inf),
	\octave, Pstutter(3, Pseq([3,4,5], inf) ),

	\overlapA, Pseq(2.pow([-1,0,1,2]), inf),
	\overlapB, Pseq(2.pow([-1,0,1].scramble), inf),

	\formantA, Pseq([1,2,3], inf),
	\formantB, Pseq([3,4,5,6,7].scramble, inf),
	\lev, Pshuf(( 1..10 ) * 0.01, inf)
).play; 
)































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
			attackTime: ( f_.linlin( 50, 1500, 0.1, 0.02) ),
			releaseTime: Rand(1,7) 
		), 
		gate: \gate.kr(1)
	);
	
	e = e * PinkNoise.ar(1!2).range( 0.1, 1 ).lag(0.02) ;

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
		*SinOsc.ar(e * 6, {Rand(0, 6.28)}.dup).range(0.87,1.15)  
		/ f,
		freq: f * 
		( formantB * SinOsc.ar(e * 8, {Rand(0, 6.28)}.dup).range(0.9,Rand(1,1.5)) ),
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
		c * ( lev * e ).pow(0.7),
		a * ( lev * e ).pow(f_.linlin( 50, 1500, 1.1, 1.5)),
		b * ( lev * e ).pow(f_.linlin( 50, 1500, 1.5, 2.5)),
	]);


	c = LeakDC.ar(c);

	d = c;	

	4.do { |i|
		d = AllpassC.ar(
			d,
			delaytime: LFNoise1.kr(0.2!2).range(0.01,0.02), 
		)
	};	

	d = c * Rand(0.1,0.6) + d; 	
	
	DetectSilence.ar(d, doneAction: 2);
	Out.ar(out, d )
}).add; 

)
( 
Pbind(
	\instrument, \sin_pulsar,
	\dur, Pseq([ 0.1,0.5,1, 2 ], inf),
	// \dur, 0.3,
	// \delta, 2,
	\degree, Pseq([ 1,3,5,7, 9, 11 ], inf),
	// \degree, 1,
	\octave, Pstutter(3, Pseq([3,4,5], inf) ),

	\overlapA, Pseq(2.pow([-1,0,1,2]), inf),
	\overlapB, Pseq(2.pow([-1,0,1].scramble), inf),

	\formantA, Pseq([1,2,3], inf),
	// \formantA, 4,
	\formantB, Pseq([3,4,5,6,7].scramble, inf),
	// \formantB, 16,
	\lev, Pshuf(( 1..10 ) * 0.01, inf)
	// \lev, 0.3
).play; 
)



