


( 
Ndef(\dattorro_dg_reverb).addSpec(			
	\drywet, [0.0,1.0],
	\predelay, [0.0,0.08],
	\bandwidth, [0.0,0.999999999],
	\decay, [0.0,0.999999999],
	\damping, [0.0,0.999999999],
	\input_diff_1, [0.001, 3, \exp],
	\input_diff_2, [0.001, 3, \exp],
	\decay_diff_1, [0.001, 3, \exp],
	\decay_diff_2, [0.001, 3, \exp],
);
)

( 
Ndef(\dattorro_dg_reverb, 

	{ arg 
		drywet = 1,
		predelay = 0.0,	
		input_diff_1 = 1, 
		input_diff_2 = 1, 
		bandwidth = 0.1,		// input bandwidth
		decay = 0.9,	// tank decay
		decay_diff_1 = 1,
		decay_diff_2 = 1,
		damping = 0.999; // tank bandwidth

		
		var src, input, local;
		var input_diff = [ input_diff_1, input_diff_2 ];	
		var dltimes;
		var tank0, tank1, tankdelays0, tankdelays1, outdelaysL, outdelaysR;
		var n_out_0, n_out_1, n_out_2, n_out_3, n_out_4, n_out_5, n_out_6;
		
		// input = In.ar(inBus, 2).sum;
		src = SoundIn.ar([0,1]);
		// src = Impulse.ar(0.5).dup;
		
		// input = OnePole.ar(
		// 	DelayC.ar(src.mean, 0.08, predelay), 
		// 	coef:(bandwidth - 1).abs
		// ); 
		
		input = Integrator.ar(
			DelayC.ar(src.mean * bandwidth, 0.08, predelay), 
			coef: 1 - bandwidth
		); 

		// [142,107,379,277]/29761; 
		dltimes = [ 0.0047713450488895, 0.0035953092974026, 0.012734787137529, 0.0093074829474816 ]; 


		dltimes.do { |it i|
			input = AllpassN.ar(input, it, it, decaytime: input_diff[ i.trunc(2) / 2 ]);
		};	
		///////////////////////////////////Tank///////////
		
		// [ 672, 4453, 1800, 3720  ]/ 27961
		tankdelays0 = [ 0.022579886428547, 0.1496253486106, 0.060481838647895, 0.12499579987232 ];

		// [908, 4217, 2656, 3163] / 27961
		tankdelays1 = [ 0.030509727495716, 0.14169550754343, 0.089244313027116, 0.10628003091294 ];

		local = LocalIn.ar(2);
		////////////////////////// 0 //////////////////

		n_out_1 = AllpassL.ar(
			input + local[1],
			0.4,
			// (tankdelays0[0] + SinOsc.ar(0.7,mul:0.00025)),
			(tankdelays0[0] + LFNoise2.ar(0.7,mul:0.00025)),
			decaytime: decay_diff_1
		);

		tank0 = DelayC.ar(
			n_out_1,
			tankdelays0[1],
			tankdelays0[1] 
		);
		
		// n_out_2 = OnePole.ar(tank0, 1 - damping);	
		n_out_2	= Integrator.ar(tank0 * ( 1 - damping ), damping) * decay;

		n_out_3 = AllpassL.ar( 
			n_out_2,
			tankdelays0[2],	
			tankdelays0[2],	
			decaytime: decay_diff_2
		);

		tank0 = DelayC.ar( 
			n_out_3,
			tankdelays0[3],
			tankdelays0[3] - ControlDur.ir 
		) * decay;

		////////////////////////////// 1 ///////////////

		n_out_4 = AllpassL.ar( 
			input + local[0],
			0.4,
			// (tankdelays1[0] + SinOsc.ar(0.71, mul:0.00018)),
			(tankdelays1[0] + LFNoise2.kr(0.71, mul:0.00018)),
			decaytime: decay_diff_1
		);

		tank1 = DelayC.ar( 
			n_out_4,
			tankdelays1[1],
			tankdelays1[1]
		);

		n_out_5	= Integrator.ar(tank1 * ( 1 - damping ), damping) * decay;

		n_out_6 = AllpassL.ar(
			n_out_5,
			tankdelays1[2],
			tankdelays1[2],
			decaytime: decay_diff_2
		);

		tank1 = DelayC.ar( 
			n_out_6,
			tankdelays1[3],
			tankdelays1[3] - ControlDur.ir

		) * decay;

		LocalOut.ar([
			tank0 * -1,	
			tank1 * -1
		]);

		// [266,  2974, 1913, 1996, 1990, 187,  1066] / 29761
		outdelaysL = [ 0.0089378717113, 0.099929437854911, 0.064278754074124, 0.067067638856221, 0.066866032727395, 0.0062833910150869, 0.035818688888142 ];

		// [353, 3627, 1228, 2673, 2111, 335, 121] / 29761
		outdelaysR = [ 0.011861160579282, 0.12187090487551, 0.041262054366453, 0.089815530392124, 0.070931756325392, 0.011256342192803, 0.0040657235979974 ];


		// Out.ar(out, 
		Mix([
			[	
				Mix([
					DelayN.ar( n_out_4, outdelaysL[0] ),
					DelayN.ar( n_out_4, outdelaysL[1] ),
					DelayN.ar( n_out_5, outdelaysL[2] ).neg,
					DelayN.ar( n_out_6, outdelaysL[3] ),
					DelayN.ar( n_out_1, outdelaysL[4] ).neg,
					DelayN.ar( n_out_2, outdelaysL[5] ).neg,
					DelayN.ar( n_out_3, outdelaysL[6] ).neg
				])
				, 
				Mix([
					DelayN.ar( n_out_1, outdelaysR[0] ),
					DelayN.ar( n_out_1, outdelaysR[1] ),
					DelayN.ar( n_out_2, outdelaysR[2] ).neg,
					DelayN.ar( n_out_3, outdelaysR[3] ),
					DelayN.ar( n_out_4, outdelaysR[4] ).neg,
					DelayN.ar( n_out_5, outdelaysR[5] ).neg,
					DelayN.ar( n_out_6, outdelaysR[6] ).neg
				])
			]  * drywet,
			src * ( 1-drywet ) 
		])
		// );

	}
	// .draw

).play; 


Ndef('dattorro_dg_reverb').set('decay_diff_2', 0.80686295991887, 'input_diff_1', 0.078512552440328, 'decay', 0.49735449685714, 'damping', 0.56613756557143, 'bandwidth', 0.80423280342857, 'input_diff_2', 0.1613229053115, 'drywet', 0.12169312169312, 'decay_diff_1', 0.40454960396665);
)












( 
Ndef(\dattorro_dg_reverb_0).addSpec(			
	\drywet, [0.0,1.0],
	\predelay, [0.0,0.08],
	\bandwidth, [0.0,0.999999999],
	\decay, [0.0,0.999999999],
	\damping, [0.0,0.999999999],
	\input_diff_1, [0.0001, 0.99999],
	\input_diff_2, [0.0001, 0.99999],
	\decay_diff_1, [-0.99999, 0.99999],
	\decay_diff_2, [0.0001, 0.99999],
);
)



( //  closer to original algo
Ndef(\dattorro_dg_reverb_0, 

	{ arg 
		drywet = 1,
		predelay = 0.0,	
		input_diff_1 = 1, 
		input_diff_2 = 1, 
		bandwidth = 0.1,		// input bandwidth
		decay = 0.9,	// tank decay
		decay_diff_1 = 1,
		decay_diff_2 = 1,
		damping = 0.999; // tank bandwidth

		
		var src, input, local;
		var input_diff = [ input_diff_1, input_diff_2 ];	
		var dltimes;
		var tank0, tank1, tankdelays0, tankdelays1, outdelaysL, outdelaysR;
		var n_out_0, n_out_1, n_out_2, n_out_3, n_out_4, n_out_5, n_out_6;
		var sr = SampleRate.ir;	
		// input = In.ar(inBus, 2).sum;
		src = SoundIn.ar([0,1]);
		// src = Impulse.ar(0.5).dup;

		// src = PlayBuf.ar(1, ~b.bufnum, loop:1)!2;

		
		input = Integrator.ar(
			DelayC.ar(src.mean * bandwidth, 0.08, predelay), 
			coef: 1 - bandwidth
		); 

		// [142,107,379,277]/29761; 
		dltimes = [ 0.0047713450488895, 0.0035953092974026, 0.012734787137529, 0.0093074829474816 ]; 


		dltimes.do { |it i|
			input = FaustApTwoCombs.ar(input, delay: it*sr, a: input_diff[ i.trunc(2) / 2 ]);
		};	
		///////////////////////////////////Tank///////////
		
		// [ 672, 4453, 1800, 3720  ]/ 27961
		tankdelays0 = [ 0.022579886428547, 0.1496253486106, 0.060481838647895, 0.12499579987232 ];

		// [908, 4217, 2656, 3163] / 27961
		tankdelays1 = [ 0.030509727495716, 0.14169550754343, 0.089244313027116, 0.10628003091294 ];

		local = LocalIn.ar(2);
		////////////////////////// 0 //////////////////

		n_out_1 = FaustApTwoCombsBipolarA.ar(
			input + local[1],
			// (tankdelays0[0] + SinOsc.ar(0.7,mul:0.00025)),
			delay: (tankdelays0[0] + LFNoise2.ar(0.7,mul:0.00025) * sr).lag(0.1) ,
			a: decay_diff_1
		);

		tank0 = DelayC.ar(
			n_out_1,
			tankdelays0[1],
			tankdelays0[1] 
		);
		
		n_out_2	= Integrator.ar(tank0 * ( 1 - damping ), damping) * decay;

		n_out_3 = FaustApTwoCombsBipolarA2outs.ar( 
			n_out_2,
			delay: tankdelays0[2] * sr,	
			a: decay_diff_2
		);

		tank0 = DelayC.ar( 
			n_out_3[0],
			tankdelays0[3],
			tankdelays0[3] - ControlDur.ir 
		) * decay;

		////////////////////////////// 1 ///////////////

		n_out_4 = FaustApTwoCombsBipolarA.ar(
			input + local[0],
			// (tankdelays1[0] + SinOsc.ar(0.71, mul:0.00018)),
			delay: (tankdelays1[0] + LFNoise2.kr(0.71, mul:0.00018) * sr).lag(0.1),
			a: decay_diff_1
		);

		tank1	= DelayC.ar( 
			n_out_4,
			tankdelays1[1],
			tankdelays1[1] 
		);

		n_out_5	= Integrator.ar(tank1 * ( 1 - damping ), damping) * decay;

		n_out_6 = FaustApTwoCombsBipolarA2outs.ar(
			n_out_5,
			delay: tankdelays1[2] * sr,
			a: decay_diff_2
		);

		tank1 = DelayC.ar( 
			n_out_6[0],
			tankdelays1[3],
			tankdelays1[3] - ControlDur.ir 

		) * decay;

		LocalOut.ar([
			tank0,	
			tank1
		]);

		// [266,  2974, 1913, 1996, 1990, 187,  1066] / 29761
		outdelaysL = [ 0.0089378717113, 0.099929437854911, 0.064278754074124, 0.067067638856221, 0.066866032727395, 0.0062833910150869, 0.035818688888142 ];

		// [353, 3627, 1228, 2673, 2111, 335, 121] / 29761
		outdelaysR = [ 0.011861160579282, 0.12187090487551, 0.041262054366453, 0.089815530392124, 0.070931756325392, 0.011256342192803, 0.0040657235979974 ];


		// Out.ar(out, 
		Mix([
			[	
				Mix([
					DelayN.ar( n_out_4, outdelaysL[0] ),
					DelayN.ar( n_out_4, outdelaysL[1] ),
					DelayN.ar( n_out_5, outdelaysL[2] ).neg,
					DelayN.ar( n_out_6[1], outdelaysL[3] ),
					DelayN.ar( n_out_1, outdelaysL[4] ).neg,
					DelayN.ar( n_out_3[1], outdelaysL[5] ).neg,
					DelayN.ar( n_out_3[0], outdelaysL[6] ).neg
				])
				, 
				Mix([
					DelayN.ar( n_out_1, outdelaysR[0] ),
					DelayN.ar( n_out_1, outdelaysR[1] ),
					DelayN.ar( n_out_2, outdelaysR[2] ).neg,
					DelayN.ar( n_out_3[1], outdelaysR[3] ),
					DelayN.ar( n_out_4, outdelaysR[4] ).neg,
					DelayN.ar( n_out_6[1], outdelaysR[5] ).neg,
					DelayN.ar( n_out_6[0], outdelaysR[6] ).neg
				])
			]  * drywet,
			src * ( 1-drywet ) 
		])
		// );

	}
	// .draw

).play; 

// orig
Ndef('dattorro_dg_reverb_0').set('decay_diff_2', 0.50269021164021, 'input_diff_1', 0.74604968253968, 'decay', 0.49735449685714, 'damping', 0.56613756557143, 'bandwidth', 0.79894179814286, 'input_diff_2', 0.61907952380952, 'drywet', 0.11640211640212, 'decay_diff_1', -0.7037262962963);

)







(
{ 
	AllpassN.ar(Impulse.ar(1),delaytime: 0.01, decaytime:MouseX.kr(0,1).poll) !2
}.play;
)


(
{ 
	FaustApTwoCombs.ar(
		Impulse.ar(1),delay: 0.01 * SampleRate.ir, a: 0.95)!2
}.play

)
















(
Ndef(\src, {
	var t = Dust.kr(0.5);
	RHPF.ar(
		CombC.ar(
			t,
			delaytime: ( 
				TExpRand.kr(120, 1800, t).lag( TExpRand.kr(0.001,0.5, t) ) 
				+ LFDNoise3.ar(EnvGen.kr(Env.perc(0.1, 2, level:7), gate:t)!2, 2) 
			).reciprocal, 
			decaytime: TExpRand.kr(1.2, 8, t)
		) *0.5,
		LFNoise1.kr(0.3!2).range(120,500),
		0.5
	)
		
}).play;
)









( 
Ndef(\refl_dattorro_dg_reverb).addSpec(			
	\dry, [0.0,1.0],
	\er, [0.0,1.0],
	\dry_er_send, \bipolar,
	\tail, [0.0,1.0],
	\predelay, [0.0,0.08],
	\bandwidth, [0.0,0.999999999],
	\decay, [0.0,0.999999999],
	\damping, [0.0,0.999999999],
	\input_diff_1, [0.001, 3, \exp],
	\input_diff_2, [0.001, 3, \exp],
	\decay_diff_1, [0.001, 3, \exp],
	\decay_diff_2, [0.001, 3, \exp],
	\tdelscale, \unipolar,
	\mod, [0.0,1],
	\tank_hpf, \freq,
	
	\erQ, [0.1,10],
	\lpfRefl, [0.0,0.9999],
	\hpfRefl, \freq

);
)
Ndef(\refl_dattorro_dg_reverb).clear;
( 

Ndef(\refl_dattorro_dg_reverb, 

	{ arg 
		predelay = 0.0,	
		input_diff_1 = 1, 
		input_diff_2 = 1, 
		bandwidth = 0.1,		// input bandwidth
		decay = 0.9,	// tank feedback
		decay_diff_1 = 1,
		decay_diff_2 = 1,
		damping = 0.999, // tank bandwidth
		tdelscale = 1,
		mod = 0.05,
		tank_hpf = 100
		; 

		
		var er, src, input, local;
		var input_diff = [ input_diff_1, input_diff_2 ];	
		var dltimes;
		var tank0, tank1, tankdelays0, tankdelays1, outdelaysL, outdelaysR;
		var n_out_0, n_out_1, n_out_2, n_out_3, n_out_4, n_out_5, n_out_6;
		
		var seed, seedChanged;
        RandID.ir(rrand(1,63));
        seed = \seed.kr(1234);
        seedChanged = Changed.kr(seed, 0.5);
		RandSeed.ir(1 - seedChanged,seed );



		// src = SoundIn.ar([0,1]);
		src = Ndef(\src).ar;			// src = PlayBuf.ar(1, ~b.bufnum, loop:1)!2;
		er = Reflector.ar(
			input: src * 0.8, 
			numReflcs: 5, 
			delayOffset: 0.01, 
			scaleDelays: 1, 
			spread: 1, 
			reflPan: Rand(-1,1),
			lpfRefl: \lpfRefl.kr(0.7), 
			hpfRefl: \hpfRefl.kr(40),
		);
		
		
		input = Integrator.ar(
			// DelayC.ar(er.mean * bandwidth, 0.08, predelay), 
			DelayC.ar(LinXFade2.ar(src.mean, er.mean, \dry_er_send.kr(-1)) * bandwidth, 0.08, predelay), 
			coef: 1 - bandwidth
		); 

		// [142,107,379,277]/29761; 
		dltimes = [ 0.0047713450488895, 0.0035953092974026, 0.012734787137529, 0.0093074829474816 ]; 


		dltimes.do { |it i|
			input = AllpassN.ar(input, it, it, decaytime: input_diff[ i.trunc(2) / 2 ]);
		};	
		///////////////////////////////////Tank///////////
		
		// [ 672, 4453, 1800, 3720  ]/ 27961
		tankdelays0 = [ 
			0.022579886428547, 
			0.1496253486106 * tdelscale, 
			0.060481838647895, 
			0.12499579987232 * tdelscale
		];

		// [908, 4217, 2656, 3163] / 27961
		tankdelays1 = [ 
			0.030509727495716, 
			0.14169550754343 * tdelscale, 
			0.089244313027116, 
			0.10628003091294 * tdelscale 
		];

		local = LocalIn.ar(2);
		////////////////////////// 0 //////////////////

		n_out_1 = AllpassC.ar(
			input + local[1],
			0.4,
			// (tankdelays0[0] + SinOsc.ar(0.7,mul:0.00025)),
			(tankdelays0[0] + LFNoise2.ar(mod,mul:0.00025)),
			decaytime: decay_diff_1
		);

		tank0 = DelayC.ar(
			n_out_1.neg,
			tankdelays0[1] * 2,
			tankdelays0[1] + LFNoise2.ar(mod,mul:tankdelays0[1]*mod),
		);
		
		n_out_2	= Integrator.ar(tank0 * ( 1 - damping ), damping) * decay;

		n_out_3 = AllpassC.ar( 
			HPF.ar( n_out_2, tank_hpf ),
			tankdelays0[2] * 2,	
			tankdelays0[2] + LFNoise2.ar(mod,mul:tankdelays0[2]*mod),	
			decaytime: decay_diff_2
		);

		tank0 = DelayC.ar( 
			n_out_3,
			tankdelays0[3] * 2,
			tankdelays0[3] - ControlDur.ir + LFNoise2.ar(mod,mul:tankdelays0[3]*mod)
		) * decay;

		////////////////////////////// 1 ///////////////

		n_out_4 = AllpassC.ar( 
			input + local[0],
			0.4,
			// (tankdelays1[0] + SinOsc.ar(0.71, mul:0.00018)),
			(tankdelays1[0] + LFNoise2.kr(mod, mul:0.00018)),
			decaytime: decay_diff_1
		);

		tank1 = DelayC.ar( 
			n_out_4.neg ,
			tankdelays1[1] * 2,
			tankdelays1[1] + LFNoise2.ar(mod,mul:tankdelays1[1]*mod)
		);

		n_out_5	= Integrator.ar(tank1 * ( 1 - damping ), damping) * decay;

		n_out_6 = AllpassC.ar(
			HPF.ar( n_out_5, tank_hpf ) * decay,
			tankdelays1[2]*2,
			tankdelays1[2] + LFNoise2.ar(mod,mul:tankdelays1[2] *mod),
			decaytime: decay_diff_2
		);

		tank1 = DelayC.ar( 
			n_out_6,
			tankdelays1[3]*2,
			tankdelays1[3] - ControlDur.ir+ LFNoise2.ar(mod,mul:tankdelays1[3]*mod)

		) * decay;

		LocalOut.ar([
			tank0 * -1,	
			tank1 * -1
		]);

		// [266,  2974, 1913, 1996, 1990, 187,  1066] / 29761
		outdelaysL = [ 0.0089378717113, 0.099929437854911, 0.064278754074124, 0.067067638856221, 0.066866032727395, 0.0062833910150869, 0.035818688888142 ];

		// [353, 3627, 1228, 2673, 2111, 335, 121] / 29761
		outdelaysR = [ 0.011861160579282, 0.12187090487551, 0.041262054366453, 0.089815530392124, 0.070931756325392, 0.011256342192803, 0.0040657235979974 ];


		// Out.ar(out, 
		Mix([
			[	
				Mix([
					DelayN.ar( n_out_4, outdelaysL[0] ),
					DelayN.ar( n_out_4, outdelaysL[1] ),
					DelayN.ar( n_out_5, outdelaysL[2] ).neg,
					DelayN.ar( n_out_6, outdelaysL[3] ),
					DelayN.ar( n_out_1, outdelaysL[4] ).neg,
					DelayN.ar( n_out_2, outdelaysL[5] ).neg,
					DelayN.ar( n_out_3, outdelaysL[6] ).neg
				])
				, 
				Mix([
					DelayN.ar( n_out_1, outdelaysR[0] ),
					DelayN.ar( n_out_1, outdelaysR[1] ),
					DelayN.ar( n_out_2, outdelaysR[2] ).neg,
					DelayN.ar( n_out_3, outdelaysR[3] ),
					DelayN.ar( n_out_4, outdelaysR[4] ).neg,
					DelayN.ar( n_out_5, outdelaysR[5] ).neg,
					DelayN.ar( n_out_6, outdelaysR[6] ).neg
				])
			]  * \tail.kr,
			src * \dry.kr,
			er * \er.kr,
		])
		// );

	}


).play; 

)

Ndef(\refl_dattorro_dg_reverb).set('decay_diff_2', 0.80686295991887, 'input_diff_1', 0.078512552440328, 'decay', 0.49735449685714, 'damping', 0.56613756557143, 'bandwidth', 0.80423280342857, 'input_diff_2', 0.1613229053115, 'drywet', 0.12169312169312, 'decay_diff_1', 0.30454960396665);

// Ndef('refl_dattorro_dg_reverb').set('decay_diff_2', 0.50269021164021, 'input_diff_1', 0.74604968253968, 'decay', 0.49735449685714, 'damping', 0.56613756557143, 'bandwidth', 0.79894179814286, 'input_diff_2', 0.61907952380952, 'drywet', 0.11640211640212, '_diff_1', 0.7037262962963);







/* 
	// these are to be compiled with Faust online compiler https://fausteditor.grame.fr/
	// used in Ndef(\dattorro_dg_reverb_0)

	declare name "ApTwoCombs";

	import("stdfaust.lib");

	// https://ccrma.stanford.edu/~jos/pasp/Allpass_Two_Combs.html#fig:fbffcf

	process(x) =  (x+_,_) ~ (@(_,del)<:_*-a,_):_*a+_  with {
	del = vslider("delay", 10,0,44100,1);
	a = vslider("a", 0.9,0,1,0.001);
	};


	//////////////////////////////////////////////////////////////////////////////////////////


	declare name "ApTwoCombsBipolarA";
	declare author "Alik Rustamoff";

	import("stdfaust.lib");

	// https://ccrma.stanford.edu/~jos/pasp/Allpass_Two_Combs.html#fig:fbffcf

	process(x) =  (x+_,_) ~ (@(_,del)<:_*-a,_):_*a+_  with {
	del = vslider("delay", 10,0,44100,1);
	a = vslider("a", 0.9,-1,1,0.001);
	};


	//////////////////////////////////////////////////////////////////////////////////////////

	
	declare name "ApTwoCombsBipolarA2outs";
	declare author "Alik Rustamoff";

	import("stdfaust.lib");

	// https://ccrma.stanford.edu/~jos/pasp/Allpass_Two_Combs.html#fig:fbffcf

	process(x) =  (x+_,_,_) ~ (_<:(@(_,del)<:_*-a,_),_):_*a+_,_  with {
	del = vslider("delay", 10,0,44100,1);
	a = vslider("a", 0.9,-1,1,0.001);
*/

};
