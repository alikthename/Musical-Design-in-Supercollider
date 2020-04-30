( 
Spec.add(\modInd0, [0,10]);
Spec.add(\modInd1, [0,10]);
Spec.add(\modInd2, [0,10]);
Spec.add(\f0ratio1, [0,8,\lin]);
Spec.add(\f0ratio2, [0,8,\lin]);
Spec.add(\gain, [0.1,10]);
Spec.add(\freq, \freq);
Spec.add(\att, \delayms);
Spec.add(\rel, [0,4]);
Spec.add(\curve, [-10,10]);
)

( 
Ndef(\fm3mc_perc,{| vel=0.5 |

	var f,ff0,a,b,car, mod,e, modInd;
	var osc,signal;
	
	f = \freq.kr(1000);

	e = EnvGen.kr(Env.perc(
		\att.kr(0.015) * Rand(0.9,1.1),
		\rel.kr(2.02), 
		curve: \curve.kr(-4)
	),
		gate: Impulse.kr(0.5),
	) * vel;


	ff0= f * ( \f0ratio1.kr(4) / \f0ratio2.kr(3)  );


	mod = SinOsc.ar(ff0) * f * \modInd0.kr(1) * e;
	car = SinOsc.ar( f + mod );

	a = HPF.ar(car * e, freq: f * 0.5);

	osc = a * \gain.kr(1) ! 2;

}).play
)


Ndef(\fm3mc_perc).gui
Ndef(\fm3mc_perc).clear
( 

Ndef(\fm3mc_perc_0, {| vel=0.5 |

	var f,ff0,ff1,ff2,a,b,c,d,e, modInd,aa;
	var osc,signal;
	f = \freq.kr(100).lag(0.01);

	e = EnvGen.kr(Env.perc(
		\att.kr(0.015) * Rand(0.9,1.1),
		\rel.kr(1.02), 
		curve: \curve.kr(-4)
	),
		gate: Impulse.kr(0.5),
	) * vel;


	ff0= f * \f0ratio.kr(2);
	ff1= f * \f1ratio.kr(2);
	


	b = SinOsc.ar(ff1) * f * \modInd1.kr(1) * e;
	c = SinOsc.ar(ff0) * f * \modInd0.kr(1) * e;
	a = SinOsc.ar(f + [b,c].sum 
	);

	a = HPF.ar(a*e, f * 0.5);



	osc = a * \pgain.kr(1);

}).gui

)



( 

SynthDef(\fm3mc_perc,{| gate=1 vel=0.7 |

	var f,ff0,ff1,ff2,a,b,c,d,e, modInd,t, aa;
	var osc,signal;
	
	f = \freq.kr(100);
	// f = f.clip(20,900);

	e = EnvGen.kr(Env.perc(
		Rand(0.8,1.2)* \att.kr(0.015),  
		\rel.kr(4.02), 
		curve: \curve.kr(-4)
	),
		gate: gate,
		doneAction:2
	) * vel;

	e = e * PinkNoise.ar(1!2).range( 0.1, 1 ).lag(0.01);

	ff0= f * \f0ratio.kr(2) 
	+ { Rand(-3,3) }.dup 
	;
	ff1= f * \f1ratio.kr(2) 
	+ { Rand(-3,3) }.dup 
	;
	ff2= f * \f2ratio.kr(4) 
	+ { Rand(-3,3) }.dup 
	;	


	b = SinOsc.ar(ff0) * f * \modInd0.kr(1) * e.pow(0.8);
	c = SinOsc.ar(ff1) * f * \modInd1.kr(0.6) * e.pow(1.4);
	d = SinOsc.ar(ff2) * f * \modInd2.kr(0.3) * e.pow(2.5);
	a = SinOsc.ar(
		f 
		+ LFTri.ar(e.pow(0.5) * LFNoise1.kr(0.3).range(1,5), Rand(0.0,2pi), mul: e.pow(0.2) *  f * 0.005)
		+ [b,c,d].sum 
		+ WhiteNoise.ar(f/8!2).lag(0.001) 
	);

	a = HPF.ar(a*e.pow(0.5), f);
	a = MidEQ.ar(a, f*\midEQratio.kr(5), rq:e.pow(0.5)* \rq.kr(3.5) + 0.1, db: \boostAtten.kr(-32));



	osc = a * \pgain.kr(0.1);
	osc = OnePole.ar(osc, \onepole.kr(0.8));
	Out.ar(0,osc);
	

}).add

)


( 
Pbind(
	\instrument, \fm3mc_perc,
	\dur, Pseq( 2.pow((-4..1).scramble), inf ),
	\legato, Pfunc({rrand(0.01,1)}),
	\degree, Pseq([0,2,5,6,8,11].scramble, inf),
	\octave, Pstutter(3, Pseq([3,4,5], inf) ),
	\vel, Pstutter(6, Pseq([ 0.2, 1, 1.2 ].scramble, inf)),
	\f0ratio, Pseq( (3..5).scramble, inf ),
	\f1ratio, Pseq( (1..3).scramble, inf ),
	\f2ratio, Pseq( (3..5) / (1..3).scramble, inf ),
	\modInd0, Pfunc{rrand(0,2.0)},
	\modInd1, Pfunc{rrand(0,1.0)},
	\modInd2, Pfunc{rrand(0,0.5)},
).play; 
)

