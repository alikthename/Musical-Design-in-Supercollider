


Ndef('barr_verb_one_FDN').clear;

( 
// [ localout[0], localout[1], localout[2], localout[3] ] // just for explanation
// [ 0.2, 0.1, -0.2, 0.3] // just for explanation
m = [ 
	[  0.0 ,  0.0 ,  0.0 ,  1.0  ], // input to 1st delay line
	[  1.0 ,  0.0 ,  0.0 ,  0.0  ], // input to 2nd delay line
	[  0.0 ,  1.0 ,  0.0 ,  0.0  ], // input to 3rd delay line
	[  0.0 ,  0.0 ,  1.0 ,  0.0  ], // input to 4th delay line
];


// localin = m.collect({|it i| ( m[i] * localin ).sum });



Ndef('barr_verb_one_FDN').ar(2);
Ndef('barr_verb_one_FDN', { arg 
	inAmp=0.8, 
	scaleDelays=1, 
	inDiffusion=0.05, 
	feedback=0.5,
	decay_sec=0.5,
	decay1_sec=0.5
	;
	var signal;
	// var signalIn = SoundIn.ar( (0..3) ) * inAmp; 
	var signalIn = [ Impulse.ar(0.5)* inAmp;  ] ++ DC.ar(0).dup(3) ;
	var tank;
	var sr = SampleRate.ir, sz = 4;
	var src, localin;
	var  times, dtimes;
	var l,r;
	var earlyDiffDTimes, earlyDiffSize=4;

	// signalIn = signalIn.rotate(0);

	// signalIn.debug(\signalIn________________);

	src = HPF.ar(signalIn, \inputHPF.kr(150));
	src = OnePole.ar(src, \inputLPF.kr(0.5));


	src = DelayN.ar(src, 0.1, \predelay.kr(0.0));

	


	times = [ 1447, 727, 613, 673, 1439, 2083, 2011, 1511, 1493, 1277, 2437, 2383, 2341, 997, 1061, 2039, 1997, 1481, 1627, 1129, 743, 983, 1091, 907, 541, 2393, 1801, 2081, 1787, 1453, 977, 2311, 691, 479, 2377, 1693, 1013, 1931, 1049, 2243, 839, 739, 1747, 601, 1823, 1123, 2467, 1297, 1613, 1361, 2207, 593, 619, 1709, 449, 937 ];




	earlyDiffDTimes = { times.removeAt(times.minIndex) } ! ( earlyDiffSize * src.size );

	l = times.size.div(2).collect({|i| times[i*2]}) * scaleDelays;
	r = times.size.div(2).collect({|i| times[i*2 + 1]}) * scaleDelays;




	dtimes = Routine{(earlyDiffDTimes ++ l ++ r).do { |it|it.yield}};
	// dtimes = Pseq( earlyDiffDTimes ++ l ++ r ).asStream;

	
	
	
	src.do { |it i|
		earlyDiffSize.do { 
			var dt = dtimes.next / sr; 
			src[i] = AllpassN.ar(src[i], dt, dt, inDiffusion);
		};
		
	};	


	localin = LocalIn.ar(sz);

	// multiplying signals by matrix
	localin = m.collect({|it i| m[i].collect({|item j| item * localin[j] }).sum });
	// localin = m.collect({|it i| ( m[i] * localin ).sum });
	

	
	tank = sz.collect { |i| 
		var dt0, dt1, dt2;
		
		localin[i] = localin[i] + src[i];
		
		dt0 = dtimes.next / sr;
		dt1 = dtimes.next / sr;
		dt2 = dtimes.next / sr;

		localin[i] = AllpassC.ar(localin[i], dt0, dt0, decay_sec );
		localin[i] = AllpassC.ar(localin[i], dt1, dt1, decay1_sec);


		localin[i] = DelayC.ar(localin[i], dt2, dt2 - ControlDur.ir * LFNoise2.kr(0.3).range(0.9,1));
		
		localin[i] = OnePole.ar(localin[i], \damping.kr(0.0) );
		localin[i] = localin[i] * feedback;
		localin[i]
	};
	

	
	LocalOut.ar(tank 

		// [ // efficient variant without matrix
		// 	tank[3],
		// 	tank[0],
		// 	tank[1],
		// 	tank[2],
		// ]
		// or just
		// tank.rotate(1)
	);
	
	tank.debug(\tank);
	
	signal = 2.collect({|i| 
		i = i*2;
		[
			tank[i] * ( "level_" ++ i ).asSymbol.kr(0),
			tank[i+1] * ( "level_" ++ ( i+1 ) ).asSymbol.kr(0) 
		]
	}).sum;




	// signal = tank.sum;
	signal = LeakDC.ar(signal); 

	signal.debug(\signal);	
	signal * \tail.kr(1)




}
	// .draw
)
.edit
;


Ndef( 'barr_verb_one_FDN' ).addSpec( 	
	\predelay, [0.0,0.1,\lin],
	\inDiffusion, [0.001,3,\exp],
	\tail, \unipolar,
	\feedback, \unipolar,
	\inputLPF, \unipolar,
	\inputHPF,\freq,
	\peakgain, [-6.0,6.0, \lin, 0.001,0],
	\rqscale, [0.5,2],
	\level_0, \bipolar,
	\level_1, \bipolar,
	\level_2, \bipolar,
	\level_3, \bipolar,
	\decay_sec, [0.0,5],
	\decay1_sec, [0.0,5],
	\damping, [0.0,1],


);


Ndef('barr_verb_one_FDN').set('damping', 0.35449735449735, 'level_1', 1.0, 'tail', 1.0, 'inputLPF', 0.12169312169312, 'inDiffusion', 0.36078452971593, 'level_2', 0.079365079365079, 'level_3', 0.058201058201058, 'decay_sec', 0.37037037037037, 'inputHPF', 35.892048805946, 'decay1_sec', 0.29100529100529, 'feedback', 0.95238095238, 'level_0', -0.87301587301587);



)













/*

	// Hadamard 2 ---  2.pow(1)

	[ 
	[ 1,  1,]
	[ 1, -1,]
	] * sqrt(2).reciprocal 



	// Hadamard 4 ---  2.pow(2)
	[ 
	[ 1,  1,  1,  1 ],
	[ 1, -1,  1, -1 ],
	[ 1,  1, -1, -1 ],
	[ 1, -1, -1,  1 ],
	] * ( sqrt(2).reciprocal * sqrt(2).reciprocal )

*/





// Hadamard 8 ---  2.pow(3)
m = [ 
	[ 1,  1,  1,  1,  1,  1,  1,  1 ],
	[ 1, -1,  1, -1,  1, -1,  1, -1 ],
	[ 1,  1, -1, -1,  1,  1, -1, -1 ],
	[ 1, -1, -1,  1,  1, -1, -1,  1 ],
	[ 1,  1,  1,  1, -1, -1, -1, -1 ],	
	[ 1, -1,  1, -1, -1,  1, -1,  1 ],
	[ 1,  1, -1, -1, -1, -1,  1,  1 ],
	[ 1, -1, -1,  1, -1,  1,  1, -1 ],
] * ( sqrt(2).reciprocal * sqrt(2).reciprocal * sqrt(2).reciprocal )




// Hadamard 16  ---  2.pow(4)
m = [
	[ 1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1],
	[ 1,  -1,   1,  -1,   1,  -1,   1,  -1,   1,  -1,   1,  -1,   1,  -1,   1,  -1],
	[ 1,   1,  -1,  -1,   1,   1,  -1,  -1,   1,   1,  -1,  -1,   1,   1,  -1,  -1],
	[ 1,  -1,  -1,   1,   1,  -1,  -1,   1,   1,  -1,  -1,   1,   1,  -1,  -1,   1],
	[ 1,   1,   1,   1,  -1,  -1,  -1,  -1,   1,   1,   1,   1,  -1,  -1,  -1,  -1],
	[ 1,  -1,   1,  -1,  -1,   1,  -1,   1,   1,  -1,   1,  -1,  -1,   1,  -1,   1],
	[ 1,   1,  -1,  -1,  -1,  -1,   1,   1,   1,   1,  -1,  -1,  -1,  -1,   1,   1],
	[ 1,  -1,  -1,   1,  -1,   1,   1,  -1,   1,  -1,  -1,   1,  -1,   1,   1,  -1],
	[ 1,   1,   1,   1,   1,   1,   1,   1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1],
	[ 1,  -1,   1,  -1,   1,  -1,   1,  -1,  -1,   1,  -1,   1,  -1,   1,  -1,   1],
	[ 1,   1,  -1,  -1,   1,   1,  -1,  -1,  -1,  -1,   1,   1,  -1,  -1,   1,   1],
	[ 1,  -1,  -1,   1,   1,  -1,  -1,   1,  -1,   1,   1,  -1,  -1,   1,   1,  -1],
	[ 1,   1,   1,   1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,   1,   1,   1,   1],
	[ 1,  -1,   1,  -1,  -1,   1,  -1,   1,  -1,   1,  -1,   1,   1,  -1,   1,  -1],
	[ 1,   1,  -1,  -1,  -1,  -1,   1,   1,  -1,  -1,   1,   1,   1,   1,  -1,  -1],
	[ 1,  -1,  -1,   1,  -1,   1,   1,  -1,  -1,   1,   1,  -1,   1,  -1,  -1,   1],
]* ( sqrt(2).reciprocal * sqrt(2).reciprocal * sqrt(2).reciprocal * sqrt(2).reciprocal);







// ~modalDensity = [ 5813, 3547, 2797, 3613, 4003, 1657, 4007, 5711 ].sum / SampleRate.ir
// general rule is to be > 0.3 of RT
// 1 sec will most of the time be OK




Ndef(\jot_mono).clear

(
Ndef(\jot_mono, { arg ff=1200, fq=1, fdb=0;
	var sig, sr=44100, a,b,c,d,delrd, localout,input, output, deltimesSec, dtimes;
	var rt, rtw, decayCoef, sigout;


	dtimes = [ 2791, 1327, 5443, 2957, 5077, 5417, 1601, 3541, 1697, 3989, 5861, 4177, 2351, 3271, 1361, 3733];
	// dtimes = [ 5813, 3547, 2797, 3613, 4003, 1657, 4007, 5711 ] * 1.75;
	// dtimes = [ 1487, 1951, 2393, 1823, 2297, 1451, 1229, 2131 ];
	// dtimes = [ 2281, 3533, 3037, 3253, 3229, 1663, 2447, 2551 ];
	
	// dtimes = m.size.collect({|i| rrand(1500,4599).nextPrime });

	// \dtimes.ir({ rrand( 1500, 4500)} !ndelaylines)


	( dtimes.sum / 44100 ).debug(\dtimes_sum_);	// should be at least 1 sec
	
	// input = Impulse.ar(1/3) / m.size !m.size;
	input = SoundIn.ar(0) / m.size !m.size;
	// input = WhiteNoise.ar(0.1) * EnvGen.kr(Env.perc(1.1, 0.02), gate: input);
	
	// input = SoundIn.ar(\in.kr(0))
	delrd = LocalIn.ar(m.size);
	
	sig = input + delrd;
	
	// multiplying signals by matrix
	sig = m.collect({|it i| m[i].collect({|item j| item * sig[j] }).sum });

	deltimesSec = dtimes / sr;
	rt = \RT.kr(10);
	decayCoef = 0.001.pow(deltimesSec/rt);

	localout = m.size.collect({|i|
		DelayN.ar(sig[i], deltimesSec[i], deltimesSec[i] - ControlDur.ir) * decayCoef[i];
	});


	
	localout = 
	m.size.collect({|i| 
		HighShelf.ar(localout[i], ff, fq, fdb )  
	});


	LocalOut.ar(localout );

	sigout = localout.sum;

	sigout = HighShelf.ar(sigout, ff, fq, fdb.neg );

	sigout = sigout * sqrt(rt).reciprocal;	
	
	
	LeakDC.ar( sigout ) * 4 !2

})
.play
;


Ndef(\jot_mono).addSpec(
	\RT, [0.1,10.0],
	\ff, \freq,
	\fq, \rq,
	\fdb, \boostcut 
)


)


Ndef(\jot_mono).clear


(
Ndef(\src, { 
	SoundIn.ar([3,1]); 
}).play;
)














Ndef(\jot_stereo).clear;
Ndef(\jot_stereo).ar(2);
(
Ndef(\jot_stereo, { arg ff=1200, fq=1, fdb=0;
	var sig, sr=44100, a,b,c,d,delrd, localout,input, output, deltimesSec, dtimes;
	var rt, rtw, decayCoef, sigout;


	dtimes = [ 2791, 1327, 5443, 2957, 5077, 5417, 1601, 3541, 1697, 3989, 5861, 4177, 2351, 3271, 1361, 3733];
	// dtimes = [ 5813, 3547, 2797, 3613, 4003, 1657, 4007, 5711 ];
	// dtimes = [ 1487, 1951, 2393, 1823, 2297, 1451, 1229, 2131 ];
	// dtimes = [ 2281, 3533, 3037, 3253, 3229, 1663, 2447, 2551 ];
	
	// dtimes = m.size.collect({|i| rrand(1000,4599).nextPrime });




	dtimes.debug(\dtimes_);	
	dtimes.sum.debug(\dtimes_sum_);	// should be at least 1 sec
	
	// input = [ Impulse.ar(1/10)  ] ++ { DC.ar(0) }.dup(m.size-1);
	input = SoundIn.ar(( 0..m.size ));
	// input = [ SoundIn.ar( 0 ) ] ++ { DC.ar(0) }.dup(m.size-1);
	// input = input.rotate(4);
	input.collect { |it| it.source}.debug(\input);
	

	delrd = LocalIn.ar(m.size);
	
	sig = input + delrd;
	
	// multiplying signals by matrix
	sig = m.collect({|it i| m[i].collect({|item j| item * sig[j] }).sum });
	// sig = m.collect({|it i| ( m[i] * sig ).sum });

	deltimesSec = dtimes / sr;
	rt = \RT.kr(10).lag(3);
	decayCoef = 0.001.pow(deltimesSec/rt);

	localout = m.size.collect({|i|
		DelayN.ar(sig[i], deltimesSec[i], deltimesSec[i] - ControlDur.ir);
	});



	localout = 
	m.size.collect({|i| 
		HighShelf.ar(localout[i], ff, fq, fdb )  *  decayCoef[i];
	});




	LocalOut.ar(localout );

	// sigout = localout.mean;
	// sigout = 2.collect({|i| localout.scramble.removeAt(0) });

	sigout = localout.size.div(2).collect({|i| 
		i = i*2;
		[
			localout[i] * ( "level_" ++ i ).asSymbol.kr(0),
			localout[i+1] * ( "level_" ++ ( i+1 ) ).asSymbol.kr(0) 
		]
	}).sum;

	sigout = HighShelf.ar(sigout, ff, fq, fdb.neg );

	sigout = sigout * sqrt(rt).reciprocal;	


	LeakDC.ar( sigout ) 

}).play ;


Ndef(\jot_stereo).addSpec(
	\RT, [0.1,10.0],
	\ff, \freq,
	\fq, \rq,
	\fdb, \boostcut, 
	\level_0, \bipolar,
	\level_1, \bipolar,
	\level_2, \bipolar,
	\level_3, \bipolar,
	\level_4, \bipolar,
	\level_5, \bipolar,
	\level_6, \bipolar,
	\level_7, \bipolar,
	\level_8, \bipolar,
	\level_9, \bipolar,
	\level_10, \bipolar,
	\level_12, \bipolar,
	\level_13, \bipolar,
	\level_14, \bipolar,
	\level_16, \bipolar
)



)

























(

SynthDef(\jot_stereo, { arg ff=1200, fq=1, fdb=0;
	var sig, sr=44100, a,b,c,d,delrd, localout,input, output, deltimesSec, dtimes;
	var rt, rtw, decayCoef, sigout;


	// dtimes = [ 2791, 1327, 5443, 2957, 5077, 5417, 1601, 3541, 1697, 3989, 5861, 4177, 2351, 3271, 1361, 3733];
	// dtimes = [ 5813, 3547, 2797, 3613, 4003, 1657, 4007, 5711 ];
	// dtimes = [ 1487, 1951, 2393, 1823, 2297, 1451, 1229, 2131 ];
	// dtimes = [ 2281, 3533, 3037, 3253, 3229, 1663, 2447, 2551 ];



	// Hadamard 16  ---  2.pow(4)
	m = [
		[ 1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1],
		[ 1,  -1,   1,  -1,   1,  -1,   1,  -1,   1,  -1,   1,  -1,   1,  -1,   1,  -1],
		[ 1,   1,  -1,  -1,   1,   1,  -1,  -1,   1,   1,  -1,  -1,   1,   1,  -1,  -1],
		[ 1,  -1,  -1,   1,   1,  -1,  -1,   1,   1,  -1,  -1,   1,   1,  -1,  -1,   1],
		[ 1,   1,   1,   1,  -1,  -1,  -1,  -1,   1,   1,   1,   1,  -1,  -1,  -1,  -1],
		[ 1,  -1,   1,  -1,  -1,   1,  -1,   1,   1,  -1,   1,  -1,  -1,   1,  -1,   1],
		[ 1,   1,  -1,  -1,  -1,  -1,   1,   1,   1,   1,  -1,  -1,  -1,  -1,   1,   1],
		[ 1,  -1,  -1,   1,  -1,   1,   1,  -1,   1,  -1,  -1,   1,  -1,   1,   1,  -1],
		[ 1,   1,   1,   1,   1,   1,   1,   1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1],
		[ 1,  -1,   1,  -1,   1,  -1,   1,  -1,  -1,   1,  -1,   1,  -1,   1,  -1,   1],
		[ 1,   1,  -1,  -1,   1,   1,  -1,  -1,  -1,  -1,   1,   1,  -1,  -1,   1,   1],
		[ 1,  -1,  -1,   1,   1,  -1,  -1,   1,  -1,   1,   1,  -1,  -1,   1,   1,  -1],
		[ 1,   1,   1,   1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,   1,   1,   1,   1],
		[ 1,  -1,   1,  -1,  -1,   1,  -1,   1,  -1,   1,  -1,   1,   1,  -1,   1,  -1],
		[ 1,   1,  -1,  -1,  -1,  -1,   1,   1,  -1,  -1,   1,   1,   1,   1,  -1,  -1],
		[ 1,  -1,  -1,   1,  -1,   1,   1,  -1,  -1,   1,   1,  -1,   1,  -1,  -1,   1],
	]* ( sqrt(2).reciprocal * sqrt(2).reciprocal * sqrt(2).reciprocal * sqrt(2).reciprocal);



	dtimes = m.size.collect({|i| rrand(1000,4599).nextPrime });




	dtimes.debug(\dtimes_);	
	dtimes.sum.debug(\dtimes_sum_);	// should be at least 1 sec


	input = In.ar(\inBus.kr, m.size);
	// input = Impulse.ar(1/3);
	// input = SoundIn.ar(( 0..m.size ));
	// input = [ SoundIn.ar( 0 ) ] ++ { DC.ar(0) }.dup(m.size-1);
	// input = input.rotate(4);
	input.collect { |it| it.source}.debug(\input);


	delrd = LocalIn.ar(m.size);

	sig = input + delrd;

	// multiplying signals by matrix
	sig = m.collect({|it i| m[i].collect({|item j| item * sig[j] }).sum });
	// sig = m.collect({|it i| ( m[i] * sig ).sum });

	deltimesSec = dtimes / sr;
	rt = \rt.kr(10).lag(2);
	decayCoef = 0.001.pow(deltimesSec/rt);

	localout = m.size.collect({|i|
		DelayN.ar(sig[i], deltimesSec[i], deltimesSec[i] - ControlDur.ir);
	});



	localout = 
	m.size.collect({|i| 
		HighShelf.ar(localout[i], ff, fq, fdb )  *  decayCoef[i];
	});




	LocalOut.ar(localout );


	sigout = localout.size.div(2).collect({|i| 
		i = i*2;
		[
			localout[i] * -1,
			localout[i+1] 
		]
	}).sum;

	sigout = HighShelf.ar(sigout, ff, fq, fdb.neg );

	sigout = sigout * sqrt(rt).reciprocal;	


	Out.ar( \outBus.kr(0), LeakDC.ar( sigout ) )

}).load;


SynthDef(\reflector, { arg
	delayOffset = 0.02,
	scaleDelays = 1,
	spread = 1,
	reflPan = 0,
	lpfRefl = 0.9,
	hpfRefl = 110
	; 

	var numReflcs = 5;
	var delays, delayPans, reflections, pannedReflections;
	var input, filtered_input;

	input = In.ar(\inBus.kr);


	delays = Array.fill( numReflcs, { |i| Rand(0.01, 0.025) });

	delays = delays * scaleDelays;

	delayPans = Array.fill(numReflcs, { |i|
		// ( reflPan + ( spread * Rand(-1.0, 1.0) ) ).clip2(1)
		( reflPan + ( spread * [ -1.0, 1.0 ].at(i.mod(2)) ) ).clip2(1)
	});


	filtered_input = HPF.ar(input, hpfRefl);

	reflections = Array.fill( numReflcs,
		{|i| 
			DelayN.ar(
				OnePole.ar(filtered_input, lpfRefl * Rand(0.8,1)) *  Rand(-1,1),
				0.2, 
				delays.at(i)
			) 
		}
	);


	reflections.do { |it i|
		var dt0 = Rand( 0.001, 0.01 );
		var dt1 = Rand( 0.001, 0.01 );
		reflections[i] = AllpassN.ar(reflections[i], dt0, dt0, Rand(0.1,0.4));
		reflections[i] = AllpassN.ar(reflections[i], dt1, dt1, Rand(0.1,0.4));
	};



	pannedReflections = Array.fill(numReflcs,
		{|i| 
			Pan2.ar(reflections.at(i), delayPans.at(i)) 
		}
	);

	Out.ar(\outBus.kr(0), pannedReflections.sum * \amp.kr(0.5));
	Out.ar(\effBus.kr(2), reflections);
} ).load;







SynthDef(\wtable_vosc_dual_t, {| vel=0.8|
	var env, freq;
	var wtbufnumRange = [ ~wtbufnums.minItem, ~wtbufnums.maxItem  ];
	var sig;
	var fenv;
	var driveDB = \driveDB.kr(0);
	var direct = \direct.kr(0.5).lag(0.2);
	// var trg = \trg.kr(1);
	var trg = Dust.kr(1);
	// var trg = Impulse.kr(2);


	env = EnvGen.ar(
		Env.adsr( 
			TExpRand.kr(0.001, 0.1, trg), 
			TExpRand.kr(0.001, 0.1, trg), 
			TExpRand.kr(0.1, 0.6, trg), 
			TExpRand.kr(0.5,4,trg), 
			curve: TRand.kr(-5.0,4,trg)), 
		gate:trg, 
		doneAction:0
	).lag(0.1) * vel;


	env = XFade2.ar(env,env * LFDNoise3.ar(env * 20 + 1).range(0,1), env*2-1);

	// env = env * WhiteNoise.ar(MouseX.kr(0,1)!2).range(0,1.0);
	
	// fenv = env * LFNoise0.kr(TRand.kr(1,21, trg)).range(0.5,1.4).lag(TRand.kr(0.0001,0.1, trg));
	fenv = env * VOsc.ar(
		\fmodbufn.kr( rrand( wtbufnumRange[0],wtbufnumRange[1] ) ).lag(0.4), 
		// TRand.kr(1,21, trg)
		\fmodFreq.kr(11)
	).range(0.5,1.4)
	.lag(TRand.kr(0.0001,0.01, trg));


	freq = \freq.kr(111).lag(0.1);
	// freq = LFNoise0.kr(1).exprange(80,1000).lag(0.1);
	// freq = TExpRand.kr(80,1000,trg);

	sig = VOsc.ar(
		(
			VOsc.ar(\wtmodbufn.kr( rrand( wtbufnumRange[0],wtbufnumRange[1] ) ).lag(0.3), \wtmodfreq.kr(2), Rand(0,2pi), \wtmodamp.kr(1)) 
			+ 
			// ( env * [  Rand(1.5,2),  Rand(1.5,2) ])	
			( env.pow(1.4) * [ LFNoise1.kr(3).range(1.5,3), LFNoise1.kr(3).range(1.5,3)])	
			+ 
			\bufn.ar( rrand( wtbufnumRange[0],wtbufnumRange[1] ) )
		).lag(0.1).mod( wtbufnumRange[1] - wtbufnumRange[0] ) + wtbufnumRange[0],
		freq
	)  * env; 

	sig = MoogVCF.ar(
		sig * driveDB.dbamp, 
		XFade2.ar( env.pow(1.4), fenv.pow(1.4), \fmod.kr(0)) * \fltRange.kr(10000) + 50 , 
		\res.kr(0)
	);
	
	sig = sig * driveDB.neg.dbamp * 0.4; // compensate drive
	// sig = Impulse.ar(Rand(0.1,0.6)!2);

	Out.ar(\outBus.kr, sig * direct);
	Out.ar(\effBus.kr, sig * (1-direct))
	// }).play
}).add
)














~reflectorBuses = Array.fill( 3, { Bus.audio(s) });
~tankbus = Bus.audio( s, 16 );
( var i = 0.39;
{

	~sourcesGroup = Group.new;
	~reflectorGroup = Group.after(~sourcesGroup);
	~tankGroup = Group.after(~reflectorGroup);



	~sources = [
		Synth(\wtable_vosc_dual_t, [\outBus, 0, \effBus, ~reflectorBuses[0], \bufn, ~wtbufnums[21]]),
		Synth(\wtable_vosc_dual_t, [\outBus, 0, \effBus, ~reflectorBuses[1], \bufn, ~wtbufnums[44]]),
		// Synth(\wtable_vosc_dual_t, [\outBus, 0, \effBus, ~reflectorBuses[2], \bufn, ~wtbufnums[27]]),
		Synth(\wtable_vosc_dual_t, [\outBus, 0, \effBus, ~reflectorBuses[2], \bufn, ~wtbufnums[51]]),
	];


	~reflectors = ~reflectorBuses.collect({| it i| 
		Synth(
			\reflector, 
			[
				\outBus, 0, 
				\inBus, it, 
				\effBus, ~tankbus.index + ( i + 5 ),
				\amp, 0.2,
				\lpfRefl, 0.6,
				\hpfRefl, 50,
			], 
			~reflectorGroup
		) 
	});


	~tankSynth = Synth('jot_stereo', 
		[
			\inBus, ~tankbus,
			\rt, 3,
			\ff, 3200,
			\fq, 1.0,
			\fdb, -0.5
		],  
		~tankGroup);





	loop{ 
		~sources.choose.set(
			\freq,  ([0, 2, 4, 5, 7, 8, 11].choose + 60).midicps * 2.pow((-2..2).choose),
			\wtmodfreq, exprand(0.1, 2),
			\wtmodamp, exprand(0.1,4),
			\fmod, rrand(-1.0,1),
			\fmodFreq, exprand(0.1,6),
			\fltRange, rrand(1000,10000),
			\vel, rrand(0.1,0.9),	
			\direct, rrand(0.05,0.3),
		);

		
		~tankSynth.set(
			\rt, rrand(1,9).debug(\RT),
			\ff, rrand( 800,6999 ),
			\fq, rrand(0.3,1.4),
			\fdb, exprand(0.1,2).neg 
		);

		2.5.pow((-2..1).choose).wait;
	}

}.fork
)







