
Ndef('barr_verb_one').clear;
( 
Ndef('barr_verb_one').ar(2);
Ndef('barr_verb_one', { arg 
	inAmp=0.8, 
	scaleDelays=1, 
	inDiffusion=0.05, 
	feedback=0.5,
	decay_sec=0.5,
	decay1_sec=0.5
	;
	var signal;
	// var signalIn = SoundIn.ar( (0..3) ) * inAmp; 
	var signalIn = [ Impulse.ar(0.5)* inAmp;  ] ++ DC.ar(0).dup(3);
	var tank;
	var sr = SampleRate.ir, sz = 4;
	var src, localin;
	var  times, dtimes;
	var l,r;
	var earlyDiffDTimes, earlyDiffSize=4;

	// signalIn = signalIn.rotate(3);
	
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


	localin = LocalIn.ar(1);

	

	
	tank = sz.collect { |i| 
		var dt0, dt1, dt2;
		
		localin = localin + src[i];
		
		dt0 = dtimes.next / sr;
		dt1 = dtimes.next / sr;
		dt2 = dtimes.next / sr;

		localin = AllpassC.ar(localin, dt0, dt0, decay_sec );
		localin = AllpassC.ar(localin, dt1, dt1, decay1_sec);


		localin = DelayC.ar(localin, dt2, dt2 - ( ControlDur.ir / sz )* LFNoise2.kr(0.3).range(0.9,1));
		
		localin = OnePole.ar(localin, \damping.kr(0.0) );

		// localin = localin * feedback;

	};
	

	tank.debug(\tank);
	
	LocalOut.ar( tank.last * feedback;);
	
	
	signal = 2.collect({|i| 
		i = i*2;
		[
			tank[i] * ( "level_" ++ i ).asSymbol.kr(0),
			tank[i+1] * ( "level_" ++ ( i+1 ) ).asSymbol.kr(0) 
		]
	}).sum;




	// signal = tank.sum;
	signal = LeakDC.ar(signal); 

	// signal.debug(\signal);	
	signal * \tail.kr(1)




}
	// .draw
)
.edit
;


Ndef( 'barr_verb_one' ).addSpec( 	
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


Ndef('barr_verb_one').set('damping', 0.35449735449735, 'level_1', 1.0, 'tail', 1.0, 'inputLPF', 0.12169312169312, 'inDiffusion', 0.36078452971593, 'level_2', 0.079365079365079, 'level_3', 0.058201058201058, 'decay_sec', 0.37037037037037, 'inputHPF', 35.892048805946, 'decay1_sec', 0.29100529100529, 'feedback', 0.95238095238, 'level_0', -0.87301587301587);



)







Ndef('barr_verb').clear;
( 
Ndef('barr_verb').ar(2);
Ndef('barr_verb', { arg 
	inAmp=0.8, 
	scaleDelays=1, 
	inDiffusion=0.05, 
	feedback=0.5,
	decay_sec=0.5,
	decay1_sec=0.5
	// peakgain=0, 
	// rqscale=1
	;
	var signal;
	var signalIn = SoundIn.ar( (0..7) ) * inAmp; 
	// var signalIn = Impulse.ar(0.5) * inAmp!8;//  ++ DC.ar(0).dup(6);
	var tank;
	var	ndelaylines = 2;
	var sr = SampleRate.ir, sz = 4;
	var src, localin;
	var  times, dtimes, b;
	var l,r;
	var earlyDiffDTimes, earlyDiffSize=4;
	var apc;
	var tankIns;


	src = HPF.ar(signalIn, \inputHPF.kr(150));
	src = OnePole.ar(src, \inputLPF.kr(0.5));


	src = DelayN.ar(src, 0.1, \predelay.kr(0.0));

	src.debug(\src________________);
	

	


	times = [ 1447, 727, 613, 673, 1439, 2083, 2011, 1511, 1493, 1277, 2437, 2383, 2341, 997, 1061, 2039, 1997, 1481, 1627, 1129, 743, 983, 1091, 907, 541, 2393, 1801, 2081, 1787, 1453, 977, 2311, 691, 479, 2377, 1693, 1013, 1931, 1049, 2243, 839, 739, 1747, 601, 1823, 1123, 2467, 1297, 1613, 1361, 2207, 593, 619, 1709, 449, 937 ];



	// times = [ 1061, 1637, 1487, 307, 1789, 2131, 823, 947, 743, 521, 1433, 601, 1093, 2029, 1361, 1229, 2143, 241, 541, 1721, 1889, 1493, 1777, 509, 839, 1913, 641, 1619, 271, 1823, 283, 863, 1601, 1693, 1867, 1117, 1193, 691, 1709, 1499, 1481, 953, 607, 1297, 1409, 331, 491, 677, 2203, 419, 733, 1831, 991, 857, 1583, 2141 ]

	// times = [ 709, 2221, 1901, 3023, 3943, 1459, 1583, 257, 2251, 2617, 857, 223, 3761, 1213, 829, 353, 929, 1597, 4001, 953, 3643, 2671, 607, 3137, 3121, 349, 739, 271, 3793, 773, 2731, 1553, 2143, 1877, 3607, 1741, 4159, 1021, 3041, 2017, 3037, 2459, 997, 2999, 2083, 3517, 499, 2803, 1013, 4027, 227, 2213, 3463, 2887, 2719, 3623 ]

	// times = [ 4447, 1511, 2797, 4229, 3209, 3881, 661, 4339, 1867, 739, 1291, 457, 4297, 1861, 3709, 3253, 487, 1709, 1607, 859, 757, 4289, 1447, 1367, 4451, 2971, 967, 773, 2137, 809, 1753, 2221, 3929, 2467, 2381, 1153, 2777, 1847, 3079, 2039, 1831, 521, 2767, 641, 2693, 2659, 3257, 1999, 1279, 2287, 1877, 1277, 569, 983, 1031, 3499 ];
	


	


	times.debug(\times_______________________________);


	earlyDiffDTimes = { times.removeAt(times.minIndex) } ! ( earlyDiffSize * src.size );

	l = times.size.div(2).collect({|i| times[i*2]}) * scaleDelays;
	r = times.size.div(2).collect({|i| times[i*2 + 1]}) * scaleDelays;



	/*
		// Keith Barr dtimes

		l = nil ! ( sz * 3  ) ;
		l.size.div(3).do { |i|
		i = i * 3;
		l[i] = rrand(1000,2000);
		l[i+1] = rrand(1000,2000);
		l[i+2] = ( l[i] + l[i+1] * rrand(0.8,1.0) ).round(1);
		};

		l.debug(\l);

		r = nil ! ( sz * 3  ) ;
		r.size.div(3).do { |i|
		i = i * 3;
		r[i] = rrand(1000,2000);
		r[i+1] = rrand(1000,2000);
		r[i+2] = ( r[i] + r[i+1] * rrand(0.8,1.0) ).round(1);
		};

		r.debug(\r);
	*/



	dtimes = Routine{(earlyDiffDTimes ++ l ++ r).do { |it|it.yield}};
	// dtimes = Pseq( earlyDiffDTimes ++ l ++ r ).asStream;

	
	
	
	src.do { |it j|
		earlyDiffSize.do { |i|
			var dt = dtimes.next / sr; 
			src[j] = AllpassN.ar(src[j], dt, dt, inDiffusion);
		};
		
	};	



	localin = LocalIn.ar(ndelaylines);

	tankIns = [nil!src.size.div(ndelaylines), nil!src.size.div(ndelaylines)];
	
	ndelaylines.do { |j|
		tankIns[j].do { |it i|
			tankIns[j][i] = src[i*2+j];
		};
	};

	tankIns.debug(\tankIns);	


	
	tank = 
	ndelaylines.collect({|j| 
		var processed; 
		
		processed = localin[j];

		sz.collect { |i| 
			var dt0, dt1, dt2;
			processed = processed + tankIns[j][i];
			
			dt0 = dtimes.next / sr;
			dt1 = dtimes.next / sr;
			dt2 = dtimes.next / sr;

			processed = FaustAllpassFComb5Decay.ar(
				in1: processed, 
				max_delay_sec: dt0 ,
				delay_sec: dt0, 
				decay_sec: decay_sec,
			);

			processed = FaustAllpassFComb5Decay.ar(
				in1: processed, 
				max_delay_sec: dt1 ,
				delay_sec: dt1, 
				decay_sec: decay_sec,
			);

			// processed = AllpassC.ar(processed, dt0, dt0, decay_sec );
			// processed = AllpassC.ar(processed, dt1, dt1, decay1_sec);


			processed = DelayC.ar(processed, dt2, dt2 * LFNoise2.kr(0.3).range(0.9,1));
			
			processed = OnePole.ar(processed, \damping.kr(0.0) );
			// processed = BPeakEQ.ar(processed, Rand(800,2500), Rand(0.9,1.2) * rqscale, db:peakgain);

		}
	});
	

	tank.debug(\tank);
	
	LocalOut.ar(
		[
			tank[0].last , 
			tank[1].last
		]//.reverse
		* feedback 
	);
	
	tank = LeakDC.ar(tank); 
	
	signal = tank.collect({|item i| 
		tank[i].collect({|it j|  
			tank[i][j] * ( "level_" ++ i ++ j ).asSymbol.kr(0) 
		}) 
	});

	signal = signal.collect({|item| item.sum}); 


	// signal.debug(\signal);	
	signal * \tail.kr(1)




}
	// .draw
)
.edit
;


Ndef( 'barr_verb' ).addSpec( 	
	\predelay, [0.0,0.1,\lin],
	\inDiffusion, [0.001,3,\exp],
	\tail, \unipolar,
	\feedback, \unipolar,
	\inputLPF, \unipolar,
	\inputHPF,\freq,
	\peakgain, [-6.0,6.0, \lin, 0.001,0],
	\rqscale, [0.5,2],
	\level_00, \bipolar,
	\level_01, \bipolar,
	\level_02, \bipolar,
	\level_03, \bipolar,
	\level_10, \bipolar,
	\level_11, \bipolar,
	\level_12, \bipolar,
	\level_13, \bipolar,
	\decay_sec, [0.0,5],
	\decay1_sec, [0.0,5],
	\damping, [0.0,1],


);




// Ndef('barr_verb').set('level_00', -0.65079365079365, 'scaleDelays', 0.46728267956841, 'inDiffusion', 0.05140015479754, 'level_03', 0.0, 'level_13', -0.058201058201058, 'level_12', 0.0, 'decay_sec', 0.10582010345484, 'level_02', 0.0, 'level_01', -0.0052910052910053, 'inputLPF', 0.51587301587302, 'inputHPF', 50.107274752699, 'decay1_sec', 0.29100529100529, 'feedback', 0.49206349206349, 'level_10', 1.0);

// Ndef('barr_verb').set('level_00', -0.83068783068783, 'level_11', -0.34391534391534, 'inputLPF', 0.052910052910053, 'scaleDelays', 1.0, 'inAmp', 1.3571429, 'inDiffusion', 0.42740320521513, 'level_03', -0.047619047619048, 'level_13', -0.61904761904762, 'damping', 0.21693121693122, 'level_12', 0.38624338624339, 'decay_sec', 1.7460317460317, 'level_02', 0.72486772486772, 'inputHPF', 50.107274752699, 'level_01', -0.57671957671958, 'decay1_sec', 0.95238095238095, 'level_10', 1.0, 'feedback', 0.72486772486772);

// ringy
Ndef('barr_verb').set('level_00', -1.0, 'level_11', 0.0, 'inputLPF', 0.5, 'inAmp', 0.8, 'scaleDelays', 1.0, 'tail', 1.0, 'inDiffusion', 0.25007699878729, 'level_03', 0.0, 'level_13', 0.0, 'level_12', 0.0, 'decay_sec', 0.5, 'level_02', 0.0, 'inputHPF', 60.154187092428, 'predelay', 0.0, 'level_01', 0.0, 'decay1_sec', 0.5, 'level_10', 1.0, 'feedback', 0.0);
)










({
var a, b, c;
var dt0 = 0.01;
var dt1 = 0.03;
a = Impulse.ar(1);
a = FaustAllpassFComb5Decay.ar(
	in1: a, 
	decay_sec: MouseX.kr(0,2).poll(1,\decay), 
	delay_sec: dt0, 
	max_delay_sec: dt0 
);

a = FaustAllpassFComb5Decay.ar(
	in1: a, 
	delay_sec:dt1, 
	decay_sec:MouseY.kr(0,2).poll(1,\decay1), 
	max_delay_sec:dt1 
);

Out.ar(0, a);

}.play )





({
var a, b, c;
var dt0 = 0.01;
var dt1 = 0.03;
a = Impulse.ar(1);
a = FaustNestedAllpassFComb5Decay.ar(
	in1: a, 
	delay_sec:dt0, 
	delay1_sec:dt1, 
	decay_sec:MouseX.kr(0,2).poll(1,\decay), 
	decay1_sec:MouseY.kr(0,2).poll(1,\decay1), 
	max_delay_sec:dt0 ,
	max_delay1_sec:dt1, 
);

Out.ar(0, a);

}.play )







/* faust source code

	declare name "nestedallpassfcomb5decay";
	declare author "alik rustamoff";

	import("stdfaust.lib");

	allpass_fcomb5(md,d,gcoef) = (+ <: de.fdelay5(md,d-1),*(gcoef)) ~ *(-gcoef) : mem,_ : +;

	process = (+ <: de.fdelay5(maxdel1 * ma.sr, (del1 * ma.sr) - 1 ),_ : allpass_fcomb5(maxdel * ma.sr, del * ma.sr, g),*(g1)) ~ *(-g1) : mem,_ : +


	with{
	maxdel = vslider( "max_delay_sec", 0.2, 0.0, 1.0, 0.001 );
	del = vslider( "delay_sec", 0.2, 0.0, 1.0, 1 / 44100 ) ;
	decay = vslider( "decay_sec", 1.0, -10.0, 10.0, 0.001 );
	g = ( 0.001 ^ ( del / abs(decay)  ) ) * ma.signum(decay);


	maxdel1 = vslider( "max_delay1_sec", 0.2, 0.0, 1.0, 0.001 );
	del1 = vslider( "delay1_sec", 0.2, 0.0, 1.0, 1 / 44100 ) ;
	decay1 = vslider( "decay1_sec", 1.0, -10.0, 10.0, 0.001 );
	g1 = ( 0.001 ^ ( del1 / abs(decay1)  ) ) * ma.signum(decay1 );
	};
*/





/* faust source code

	declare name "AllpassFComb5Decay";

	import("stdfaust.lib");

	allpass_fcomb5(maxdel,del,g) = (+ <: de.fdelay5(maxdel * ma.SR, (del * ma.SR) -1),*(g)) ~ *(-g) : mem, _ : +;


	process = allpass_fcomb5(maxdel, del, g) with{
	maxdel = vslider( "max_delay_sec", 0.2, 0.0, 1.0, 0.001 );
	del = vslider( "delay_sec", 0.2, 0.0, 1.0, 1 / 44100 ) ;
	decay = vslider( "decay_sec", 1.0, -10.0, 10.0, 0.001 );
	g = ( 0.001 ^ ( del / abs(decay)  ) ) * ma.signum(decay);
	//g = vslider("g", 0.9,-1,1,0.001);

	};

*/



















Ndef('barr_verb_nested').clear;
( 
Ndef('barr_verb_nested').ar(2);
Ndef('barr_verb_nested', { arg 
	inAmp=0.8, 
	scaleDelays=1, 
	inDiffusion=0.05, 
	feedback=0.5,
	decay_sec=0.5,
	decay1_sec=0.5
	// peakgain=0, 
	// rqscale=1
	;
	var signal, signalIn = SoundIn.ar( (0..7) ) * inAmp; 
	var tank;
	var	ndelaylines = 2;
	var sr = SampleRate.ir, sz = 4;
	var src, localin;
	var  times, dtimes, b;
	var l,r;
	var earlyDiffDTimes, earlyDiffSize=4;
	var apc;
	var tankIns;


	src = HPF.ar(signalIn, \inputHPF.kr(150));
	src = OnePole.ar(src, \inputLPF.kr(0.5));


	src = DelayN.ar(src, 0.1, \predelay.kr(0.0));

	src.debug(\src________________);
	

	


	times = [ 1447, 727, 613, 673, 1439, 2083, 2011, 1511, 1493, 1277, 2437, 2383, 2341, 997, 1061, 2039, 1997, 1481, 1627, 1129, 743, 983, 1091, 907, 541, 2393, 1801, 2081, 1787, 1453, 977, 2311, 691, 479, 2377, 1693, 1013, 1931, 1049, 2243, 839, 739, 1747, 601, 1823, 1123, 2467, 1297, 1613, 1361, 2207, 593, 619, 1709, 449, 937 ];



	// times = [ 1061, 1637, 1487, 307, 1789, 2131, 823, 947, 743, 521, 1433, 601, 1093, 2029, 1361, 1229, 2143, 241, 541, 1721, 1889, 1493, 1777, 509, 839, 1913, 641, 1619, 271, 1823, 283, 863, 1601, 1693, 1867, 1117, 1193, 691, 1709, 1499, 1481, 953, 607, 1297, 1409, 331, 491, 677, 2203, 419, 733, 1831, 991, 857, 1583, 2141 ]

	// times = [ 709, 2221, 1901, 3023, 3943, 1459, 1583, 257, 2251, 2617, 857, 223, 3761, 1213, 829, 353, 929, 1597, 4001, 953, 3643, 2671, 607, 3137, 3121, 349, 739, 271, 3793, 773, 2731, 1553, 2143, 1877, 3607, 1741, 4159, 1021, 3041, 2017, 3037, 2459, 997, 2999, 2083, 3517, 499, 2803, 1013, 4027, 227, 2213, 3463, 2887, 2719, 3623 ]

	// times = [ 4447, 1511, 2797, 4229, 3209, 3881, 661, 4339, 1867, 739, 1291, 457, 4297, 1861, 3709, 3253, 487, 1709, 1607, 859, 757, 4289, 1447, 1367, 4451, 2971, 967, 773, 2137, 809, 1753, 2221, 3929, 2467, 2381, 1153, 2777, 1847, 3079, 2039, 1831, 521, 2767, 641, 2693, 2659, 3257, 1999, 1279, 2287, 1877, 1277, 569, 983, 1031, 3499 ];
	


	


	times.debug(\times_______________________________);


	earlyDiffDTimes = { times.removeAt(times.minIndex) } ! ( earlyDiffSize * src.size );

	l = times.size.div(2).collect({|i| times[i*2]}) * scaleDelays;
	r = times.size.div(2).collect({|i| times[i*2 + 1]}) * scaleDelays;



	/*

		// Keith Barr dtimes

		l = nil ! ( sz * 3  ) ;
		l.size.div(3).do { |i|
		i = i * 3;
		l[i] = rrand(1000,2000);
		l[i+1] = rrand(1000,2000);
		l[i+2] = ( l[i] + l[i+1] * rrand(0.8,1.0) ).round(1);
		};

		l.debug(\l);

		r = nil ! ( sz * 3  ) ;
		r.size.div(3).do { |i|
		i = i * 3;
		r[i] = rrand(1000,2000);
		r[i+1] = rrand(1000,2000);
		r[i+2] = ( r[i] + r[i+1] * rrand(0.8,1.0) ).round(1);
		};

		r.debug(\r);
	*/





	dtimes = Routine{(earlyDiffDTimes ++ l ++ r).do { |it|it.yield}};
	// dtimes = Pseq( earlyDiffDTimes ++ l ++ r ).asStream;

	
	
	
	src.do { |it j|
		earlyDiffSize.do { |i|
			var dt = dtimes.next / sr; 
			src[j] = AllpassN.ar(src[j], dt, dt, inDiffusion);
		};
		
	};	



	localin = LocalIn.ar(ndelaylines);

	tankIns = [nil!src.size.div(ndelaylines), nil!src.size.div(ndelaylines)];
	
	tankIns.do { |it j|
		tankIns[j].do { |item i|
			tankIns[j][i] = src[i*2+j];
		};
	};

	tankIns.debug(\tankIns);	


	
	tank = 
	ndelaylines.collect({|j| 
		var processed; 
		
		processed = localin[j];

		sz.collect { |i| 
			var dtsec;
			var dt0, dt1;
			processed = processed + tankIns[j][i];
			
			dt0 = dtimes.next / sr;
			dt1 = dtimes.next / sr;


			processed = FaustNestedAllpassFComb5Decay.ar(
				in1: processed, 
				max_delay_sec:dt0,
				max_delay1_sec:dt1, 
				delay_sec: dt0, 
				delay1_sec: dt1, 
				decay_sec: decay_sec,
				decay1_sec: decay1_sec,
			);




			dtsec = dtimes.next / sr;
			processed = DelayC.ar(processed, dtsec, dtsec * LFNoise2.kr(0.3).range(0.9,1));
			
			// processed = BPeakEQ.ar(processed, Rand(800,2500), Rand(0.9,1.2) * rqscale, db:peakgain);
			processed = OnePole.ar(processed, \damping.kr(0.0) );

		}
	});
	

	tank.debug(\tank);
	
	LocalOut.ar(
		[
			tank[0].last , 
			tank[1].last
		]//.reverse
		* feedback 
	);
	
	tank = LeakDC.ar(tank); 
	
	signal = tank.collect({|item i| 
		tank[i].collect({|it j|  
			tank[i][j] * ( "level_" ++ i ++ j ).asSymbol.kr(0) 
		}) 
	});

	signal = signal.collect({|item| item.sum}); 


	// signal = src;
	// signal.debug(\signal);	
	signal * \tail.kr(1)




}
	// .draw
)
.edit
;


Ndef( 'barr_verb_nested' ).addSpec( 	
	\predelay, [0.0,0.1,\lin],
	\inDiffusion, [0.001,3,\exp],
	\tail, \unipolar,
	\feedback, \unipolar,
	\inputLPF, \unipolar,
	\inputHPF,\freq,
	\peakgain, [-6.0,6.0, \lin, 0.001,0],
	\rqscale, [0.5,2],
	\level_00, \bipolar,
	\level_01, \bipolar,
	\level_02, \bipolar,
	\level_03, \bipolar,
	\level_10, \bipolar,
	\level_11, \bipolar,
	\level_12, \bipolar,
	\level_13, \bipolar,
	\decay_sec, [0.0,5],
	\decay1_sec, [0.0,5],
	\damping, [0.0,1],


);


// Ndef('barr_verb_nested').set('level_00', -0.83068783068783, 'level_11', -0.34391534391534, 'inputLPF', 0.052910052910053, 'scaleDelays', 1.0, 'inAmp', 1.3571429, 'inDiffusion', 0.42740320521513, 'level_03', -0.047619047619048, 'level_13', -0.61904761904762, 'damping', 0.21693121693122, 'level_12', 0.38624338624339, 'decay_sec', 1.7460317460317, 'level_02', 0.72486772486772, 'inputHPF', 50.107274752699, 'level_01', -0.57671957671958, 'decay1_sec', 0.95238095238095, 'level_10', 1.0, 'feedback', 0.72486772486772);

// Ndef('barr_verb_nested').set('level_00', -0.65079365079365, 'scaleDelays', 0.46728267956841, 'inDiffusion', 0.05140015479754, 'level_03', 0.0, 'level_13', -0.058201058201058, 'level_12', 0.0, 'decay_sec', 0.10582010345484, 'level_02', 0.0, 'level_01', -0.0052910052910053, 'inputLPF', 0.51587301587302, 'inputHPF', 50.107274752699, 'decay1_sec', 0.29100529100529, 'feedback', 0.49206349206349, 'level_10', 1.0);

//ringy
Ndef('barr_verb_nested').set('level_00', -1.0, 'level_11', 0.0, 'inputLPF', 0.5, 'inAmp', 0.8, 'scaleDelays', 1.0, 'tail', 1.0, 'inDiffusion', 0.25007699878729, 'level_03', 0.0, 'level_13', 0.0, 'level_12', 0.0, 'decay_sec', 0.5, 'level_02', 0.0, 'inputHPF', 60.154187092428, 'predelay', 0.0, 'level_01', 0.0, 'decay1_sec', 0.5, 'level_10', 1.0, 'feedback', 0.0);
)
























( 





SynthDef('barr_verb_nested', { arg 
	inAmp=0.8, 
	scaleDelays=1, 
	feedback=0.5,
	decay_sec=0.5,
	decay1_sec=0.5,
	peakgain=0, 
	rqscale=1
	;
	var signal, signalIn = In.ar(\inBus.kr, 8) * inAmp; 
	var tank;
	var	ndelaylines = 2;
	var sr = SampleRate.ir, sz = 4;
	var src, localin;
	var  times, dtimes, b;
	var l,r;
	var earlyDiffDTimes, earlyDiffSize=4;
	var apc;
	var tankIns;



	src = DelayN.ar(signalIn, 0.1, \predelay.kr(0.0));

	// times = [ 1447, 727, 613, 673, 1439, 2083, 2011, 1511, 1493, 1277, 2437, 2383, 2341, 997, 1061, 2039, 1997, 1481, 1627, 1129, 743, 983, 1091, 907, 541, 2393, 1801, 2081, 1787, 1453, 977, 2311, 691, 479, 2377, 1693, 1013, 1931, 1049, 2243, 839, 739, 1747, 601, 1823, 1123, 2467, 1297, 1613, 1361, 2207, 593, 619, 1709, 449, 937 ];

	// Keith Barr dtimes
	

	/*
		// Schroeder delay times span successive orders of magnitude
		3.collect { |i| 3000 / 3.pow(i).postln};
	*/


	



	

	l = nil ! ( sz * 3  ) ;
	l.size.div(3).do { |i|
		i = i * 3;
		// l[i] = rrand(300,700) * 2.pow(i/3);
		// l[i+1] = rrand(300,700) * 2.pow(i/3);
		l[i] = rrand(2500,5000) / 3.pow(i/3);
		l[i+1] = rrand(2500,5000) / 3.pow(i/3);
		l[i+2] = ( l[i] + l[i+1] * rrand(0.8,1.0) ).round(1);
	};

	l = l * scaleDelays;

	r = nil ! ( sz * 3  ) ;
	r.size.div(3).do { |i|
		i = i * 3;
		// r[i] = rrand(300,700) * 2.pow(i/3);
		// r[i+1] = rrand(300,700) * 2.pow(i/3);
		r[i] = rrand(2500,5000) / 3.pow(i/3);
		r[i+1] = rrand(2500,5000) / 3.pow(i/3);
		r[i+2] = ( r[i] + r[i+1] * rrand(0.8,1.0) ).round(1);
	};


	r = r * scaleDelays;




	// l = times.size.div(2).collect({|i| times[i*2]}) * scaleDelays;
	// r = times.size.div(2).collect({|i| times[i*2 + 1]}) * scaleDelays;
	

	dtimes = Routine{(l ++ r).do { |it|it.yield}};



	localin = LocalIn.ar(ndelaylines);

	tankIns = [nil!src.size.div(ndelaylines), nil!src.size.div(ndelaylines)];

	tankIns.do { |it j|
		tankIns[j].do { |item i|
			tankIns[j][i] = src[i*2+j];
		};
	};

	tankIns.debug(\tankIns);	



	tank = 
	ndelaylines.collect({|j| 
		var processed; 
		
		processed = localin[j];

		sz.collect { |i| 
			var dtsec;
			var dt0, dt1;
			processed = processed + tankIns[j][i];

			
			dt0 = dtimes.next / sr;
			dt1 = dtimes.next / sr;


			processed = FaustNestedAllpassFComb5Decay.ar(
				in1: processed, 
				max_delay_sec:dt0,
				max_delay1_sec:dt1, 
				delay_sec: dt0, 
				delay1_sec: dt1, 
				decay_sec: decay_sec,
				decay1_sec: decay1_sec,
			);

			dtsec = dtimes.next / sr;
			processed = DelayC.ar(processed, dtsec, dtsec * LFNoise2.kr(0.3).range(0.9,1));
			
			processed = BPeakEQ.ar(processed, \peakfreq.kr(1200).lag(0.6) * Rand(0.7,1.3), Rand(0.9,1.2) * rqscale, db:peakgain);
			processed = OnePole.ar( processed, \damping.kr(0.3) * Rand(0.8,1.1));

		}
	});


	tank.debug(\tank);

	LocalOut.ar(
		[
			tank[0].last , 
			tank[1].last
		]//.reverse
		* feedback 
	);

	tank = LeakDC.ar(tank); 

	signal = tank.collect({|item i| 
		tank[i].collect({|it j|  
			tank[i][j] * ( "level_" ++ i ++ j ).asSymbol.kr(0) 
		}) 
	});

	signal = signal.collect({|item| item.sum}); 

	Out.ar( \outBus.kr(0), signal * \tail.kr(1))


}
).add;




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
} ).add;







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
~tankbus = Bus.audio( s, 8 );
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
				\effBus, ~tankbus.index + i.mod(3),
				\amp, 0.2,
				\lpfRefl, 0.6,
				\hpfRefl, 50,
			], 
			~reflectorGroup
		) 
	});


	~tankSynth = Synth('barr_verb_nested', 
		[
			\inBus, ~tankbus,
			\inAmp, 1.0,
			'scaleDelays', 1,
			'level_00', 1.0,
			'level_01', 0.0, 
			'level_02', 0.0, 
			'level_03', 0.0, 
			'level_10', 0.0,
			'level_11', 0.0,
			'level_12', 0.0, 
			'level_13', -0.7,
			'decay0_sec', 0.69100529100529, 
			'decay1_sec', 1.30582010345484, 
			'feedback', 0.59206349206349, 
			'peakfreq', 1200,
			'peakgain', 0,
			'rqscale', 1.2,
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
			\peakfreq, exprand(110, 6000),
			\peakgain, rrand(0,1.2),
			\rqscale, rrand(0.5,1.2),
			\feedback, rrand(0.3,0.8),
			\delay_sec, rrand(0.3,1.2),
			\delay1_sec, rrand(0.3,1.2),
			\damping, rrand(0.1,0.6),
		);

		2.5.pow((-2..1).choose).wait;
	}

}.fork
)






