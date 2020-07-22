


( // PM
{
	var ph, cfr, mfr, mInd;
	cfr		= Line.kr(60,1700, 13);
	mfr			= cfr * 2;
	mInd		= 1;
	ph			= SinOsc.ar( mfr, pi * 0.5 ).range(0, mInd);
	// ph			= SinOsc.ar( mfr + 0.4, pi * 0.5 ).range(0, mInd); // overtones modulation
	// ph			= SinOsc.ar(mfr + 0.4, pi * 0.5  ).range(0, cfr.linexp(60,1700, 1, 0.3)); // mod index to carier frequency mapping
	SinOsc.ar(cfr, ph) * 0.3!2;


}.play
)


( /// FM 
{
	var fr, mfr, mInd, cfr;
	cfr			= Line.kr(60,1700, 13);
	mfr			= cfr * 2;
	mInd		= 225;
	fr			= cfr + SinOsc.ar(mfr, mul:mInd);
	SinOsc.ar(fr) * 0.3!2;
}.play
)


( /// FM: mod index multiplied by carier frequency
{
	var fr, mfr, mInd, cfr;
	cfr			= Line.kr(60,1700, 13);
	mfr			= cfr * 2;
	mInd		= cfr * 1; // !!!!
	fr			= cfr + SinOsc.ar(mfr, mul:mInd);
	SinOsc.ar(fr) * 0.3!2;
}.play
)


// fixed cfr, low mfr

( /// FM: mod index multiplied by carier frequency
{
	var fr, mfr, mInd, cfr;
	cfr			= 100;
	mfr			= cfr * 0.1;
	mInd		= cfr * 1; // !!!!
	fr			= cfr + SinOsc.ar(mfr, mul:mInd);
	SinOsc.ar(fr) * 0.3!2;
}.play
)



( // PM
{
	var ph, cfr, mfr, mInd;
	cfr		= 100;
	mfr			= cfr * 0.1;
	mInd		= 1;
	// mInd		= 20;
	ph			= SinOsc.ar(mfr + 0.4, pi * 0.5  ).range(0, mInd); // mod index to carier frequency mapping
	SinOsc.ar(cfr, ph) * 0.3!2;
	// ph


}.play
)




















///// dx7 algo 4
( // PM
/// previous operator modulates phase of next operator's modulator

SynthDef( \fm_algo4, { arg freq=111, mInd1=0.5, mInd2=0.5, mInd3=0.5, mInd4=0.5, mInd5=0.5, mInd6=0.5;
	var fr, mfr, mInd, cfr, e;
	var cascade_0, cascade_1, mix;
	
	e = EnvGen.kr(Env.perc(0.001, 2), doneAction: 2);

	cascade_0 = SinOsc.ar( freq, SinOsc.ar( freq * \ratio3.kr(1), pi * 0.5).range(0, mInd3) ) * \amp_3.kr(0.5);
	cascade_0 = SinOsc.ar( freq, SinOsc.ar( freq * \ratio2.kr(1), pi*0.5 + cascade_0.range(0,mInd2) ).range(0, mInd2) ) * \amp_2.kr(0.5);
	cascade_0 = SinOsc.ar( freq, SinOsc.ar( freq * \ratio1.kr(1), pi*0.5 + cascade_0.range(0,mInd1) ).range(0, mInd1) ) * \amp_1.kr(0.5);

	cascade_1 = SinOsc.ar( freq, SinOsc.ar( freq * \ratio6.kr(1), pi*0.5 + LocalIn.ar(1) ).range(0, mInd6) ) * \amp_6.kr(0.5);
	cascade_1 = SinOsc.ar( freq, SinOsc.ar( freq * \ratio5.kr(1), pi*0.5 + cascade_1.range(0,mInd5) ).range(0, mInd5) ) * \amp_5.kr(0.5);
	cascade_1 = SinOsc.ar( freq, SinOsc.ar( freq * \ratio4.kr(1), pi*0.5 + cascade_1.range(0,mInd4) ).range(0, mInd4) ) * \amp_4.kr(0.5);

	LocalOut.ar(cascade_1.range(0,pi) * \fb.kr(0.8));

	mix = Mix([
		cascade_0, 
		cascade_1
	]);
	mix = LeakDC.ar(mix);

	Out.ar(0, mix * e * 0.1!2)

}).add;



)




( ////// PM
/// previous operator is added to next operator's modulator
SynthDef( \fm_algo4, { arg freq=111, mInd1=0.5, mInd2=0.5, mInd3=0.5, mInd4=0.5, mInd5=0.5, mInd6=0.5;
	var fr, mfr, mInd, cfr, e;
	var cascade_0, cascade_1, mix;
	
	e = EnvGen.kr(Env.perc(0.001, 2), doneAction: 2);

	cascade_0 = SinOsc.ar( freq, SinOsc.ar( freq * \ratio3.kr(1), pi*0.5).range(0, mInd3) ) * \amp_3.kr(0.5);
	cascade_0 = SinOsc.ar( freq, SinOsc.ar( freq * \ratio2.kr(1), pi*0.5).range(0, mInd2) + cascade_0.range(0,mInd2) ) * \amp_2.kr(0.5);
	cascade_0 = SinOsc.ar( freq, SinOsc.ar( freq * \ratio1.kr(1), pi*0.5).range(0, mInd1) + cascade_0.range(0,mInd1) ) * \amp_1.kr(0.5);


	cascade_1 = SinOsc.ar( freq, SinOsc.ar( freq * \ratio6.kr(1), pi*0.5).range(0, mInd6) + LocalIn.ar(1).range(0, mInd6) ) * \amp_6.kr(0.5);
	cascade_1 = SinOsc.ar( freq, SinOsc.ar( freq * \ratio5.kr(1), pi*0.5).range(0, mInd5) + cascade_1.range(0,mInd5) ) * \amp_5.kr(0.5);
	cascade_1 = SinOsc.ar( freq, SinOsc.ar( freq * \ratio4.kr(1), pi*0.5).range(0, mInd4) + cascade_1.range(0,mInd4) ) * \amp_4.kr(0.5);

	LocalOut.ar(cascade_1 * \fb.kr(0.9));

	mix = Mix([
		cascade_0, 
		cascade_1
	]);
	
	mix = LeakDC.ar(mix);
	
	Out.ar(0, mix * e * 0.1!2)
	
}).add;



)



( ////// FM
// previous operator modulates next operator's modulator
SynthDef( \fm_algo4, { arg freq=111;
	var fr, mfr, mInd, cfr, e;
	var cascade_0, cascade_1, mix;
	
	e = EnvGen.kr(Env.perc(0.001, 2), doneAction: 2);

	cascade_0 = SinOsc.ar( freq + ( SinOsc.ar( freq * \ratio3.kr(1) ) * freq * \mInd_3.kr(0.1) ) ) * \amp_3.kr(0.5);
	cascade_0 = SinOsc.ar( freq + ( SinOsc.ar( freq * \ratio2.kr(1) + ( cascade_0 * freq ) ) * freq * \mInd_2.kr(0.5) ) ) * \amp_2.kr(0.5);
	cascade_0 = SinOsc.ar( freq + ( SinOsc.ar( freq * \ratio1.kr(1) + ( cascade_0 * freq ) ) * freq * \mInd_1.kr(0.5) ) ) * \amp_1.kr(1);

	cascade_1 = SinOsc.ar( freq + ( SinOsc.ar( freq * \ratio6.kr(1) ) + LocalIn.ar(1) * freq * \mInd_6.kr(0.5) ) ) * \amp_6.kr(0.5);
	cascade_1 = SinOsc.ar( freq + ( SinOsc.ar( freq * \ratio5.kr(1) + ( cascade_1 * freq ) ) * freq * \mInd_5.kr(0.5) ) ) * \amp_5.kr(0.5);
	cascade_1 = SinOsc.ar( freq + ( SinOsc.ar( freq * \ratio4.kr(1) + ( cascade_1 * freq ) ) * freq * \mInd_4.kr(0.5) ) ) * \amp_4.kr(1);

	LocalOut.ar(cascade_1 * \fb.kr(0.9));

	mix = Mix([
		cascade_0, 
		cascade_1
	]);
	mix = LeakDC.ar(mix);
	
	Out.ar(0, mix * e * 0.3!2)
	
}).add;



)




( ////// FM

// previous operator is added to next's modulator
SynthDef( \fm_algo4, { arg freq=111;
	var fr, mfr, mInd, cfr, e;
	var cascade_0, cascade_1, mix;
	
	e = EnvGen.kr(Env.perc(0.001, 2), doneAction: 2);

	cascade_0 = SinOsc.ar( freq + ( SinOsc.ar( freq * \ratio3.kr(1) ) * freq * \mInd_3.kr(0.1) ) ) * \amp_3.kr(0.5);
	cascade_0 = SinOsc.ar( freq + ( SinOsc.ar( freq * \ratio2.kr(1) ) + cascade_0 * freq * \mInd_2.kr(0.5) ) ) * \amp_2.kr(0.5);
	cascade_0 = SinOsc.ar( freq + ( SinOsc.ar( freq * \ratio1.kr(1) ) + cascade_0 * freq * \mInd_1.kr(0.5) ) ) * \amp_1.kr(1);

	cascade_1 = SinOsc.ar( freq + ( SinOsc.ar( freq * \ratio6.kr(1) ) + LocalIn.ar(1) * freq * \mInd_6.kr(0.5) ) ) * \amp_6.kr(0.5);
	cascade_1 = SinOsc.ar( freq + ( SinOsc.ar( freq * \ratio5.kr(1) ) + cascade_1 * freq * \mInd_5.kr(0.5) ) ) * \amp_5.kr(0.5);
	cascade_1 = SinOsc.ar( freq + ( SinOsc.ar( freq * \ratio4.kr(1) ) + cascade_1 * freq * \mInd_4.kr(0.5) ) ) * \amp_4.kr(1);

	LocalOut.ar(cascade_1 * \fb.kr(0.9));

	mix = Mix([
		cascade_0, 
		cascade_1
	]);
	mix = LeakDC.ar(mix);
	
	Out.ar(0, mix * e * 0.3!2)
	
}).add;



)















( 
Pbind(
	\instrument , \fm_algo4,
	\dur        , 1,
	\scale      , Scale.major,
	\root       , 0,
	\degree     , Pseq([0,2,4,6], inf),
	\octave     , 4
).play;
)






( 
// thisThread.randSeed = 123412315123;
// thisThread.randSeed = 512548796;
// thisThread.randSeed = 48796;
// thisThread.randSeed = 6;
thisThread.randSeed = 11;
Pbind(
	\instrument , \fm_algo4,
	\dur        , 1,
	\scale      , Scale.major,
	\root       , 0,
	\degree     , Pseq([0,2,4,6], inf),
	\octave     , 4,
	\ratio3		, rand(5) + rand(0.01) / rand(5),
	\ratio2		, rand(5) + rand(0.01) / rand(5),
	\ratio1		, rand(5) + rand(0.01) / rand(5),
	\ratio6		, rand(5) + rand(0.01) / rand(5),
	\ratio5		, rand(5) + rand(0.01) / rand(5),
	\ratio4		, rand(5) + rand(0.01) / rand(5),
	\mInd3		, rand(4.0),
	\mInd2		, rand(4.0),
	\mInd1		, rand(4.0),
	\mInd6		, rand(4.0),
	\mInd5		, rand(4.0),
	\mInd4		, rand(4.0),
	\amp3		, rand(1.0),
	\amp2		, rand(1.0),
	\amp1		, rand(1.0),
	\amp6		, rand(1.0),
	\amp5		, rand(1.0),
	\amp4		, rand(1.0),
	\fb			, 0.9
).play;
)












(

thisThread.randSeed = 98026;
// thisThread.randSeed = 198026;
// thisThread.randSeed = 6198026;
// thisThread.randSeed = 196198026;
// thisThread.randSeed = 1196198026;
// thisThread.randSeed = 1403411655;
// thisThread.randSeed = 1577108009;
// thisThread.randSeed = 1190818001;
{
	var f = [60,64,67,71].midicps;
	LeakDC.ar(
		(nil!2).collect({|it i|
			it = SinOsc.ar(f);
			[2,3].choose.do { |j| var fcoef, micoef;
				fcoef = [
					rrand(0.999,1.001),
					rrand(0.001/2.sqrt, 0.0001/2.sqrt),
					rrand(2.sqrt, 2pi)
				].wchoose([0.6,0.3, 0.1]); 
				micoef = rrand(0.1,2);
				it = SinOsc.ar(i+1 * f * fcoef
					+ (it*f*(j+1*micoef)));
				it = LeakDC.ar(it)
			};
			it.sum * 0.06;
		}) 
	);
}.play
)










