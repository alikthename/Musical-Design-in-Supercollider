


(
SynthDef("imp",{

	var a, delay;
	a = Saw.ar * EnvGen.kr(Env.perc(0.02, 0.02), doneAction: 2);
	a = a.tanh!2;
	Out.ar(\outBus.kr(0), a );

}).play
)



~reverbBus = Bus.audio(s,2);


( 
{
	loop{ 
		Synth("imp", [\outBus, ~reverbBus.index]);
		rrand(0.1,0.4).wait;
	}
}.fork;
)


( 
Spec.add(\spread, [-1.0,1]);
Spec.add(\delayOffset , [0.001,0.5]);
Spec.add(\direct , [0.0,1]);
Spec.add(\refl , [0.0,2.9]);
Spec.add(\lpfRefl , [0.0,0.999]);
Spec.add(\hpfRefl , \freq);
Spec.add(\refl_pan ,[-1.0,1]);
Spec.add(\pan0 ,[-1.0,1]);
Spec.add(\pan1 ,[-1.0,1]);
Spec.add(\pan2 ,[-1.0,1]);
Spec.add(\pan3 ,[-1.0,1]);
Spec.add(\pan4 ,[-1.0,1]);
Spec.add(\reflQ ,[0.01,1]);
)


( 
Ndef(\refl, { 
	var input;
	var numReflcs = 5 ; 

	var delays, delayPans, reflections, pannedReflections;
	var filtered_input;

	// input = In.ar(\inbus.kr(~reverbBus), 2).sum;
	input = In.ar(~reverbBus.index, 2).sum;
	// input = SoundIn.ar([0,1]);


	//array of delay times
	delays = numReflcs.collect{arg i; ( \delay++i.asSymbol ).asSymbol.kr(0.02 + (i * rrand(0.004, 0.007)))};

	delays = delays * \scaleDelays.kr(1);


	filtered_input = HPF.ar( 
		OnePole.ar( input, \lpfRefl.kr( 0.1) ), 
		\hpfRefl.kr( 150) 
	);


	reflections = Array.fill(numReflcs,
		{|i| 
			DelayN.ar(
				BPF.ar(
					// filtered_input * (IRand(0,1) - 0.5 * 2 / (i+1 )), 
					filtered_input / (i+1 ), 
					freq: ExpRand(300, 2000), 
					rq: \reflQ.kr(0.5)
				),
				0.2, 
				delays.at(i)
			) 
		}
	);


	delayPans = Array.fill(numReflcs, { arg i;
		// Rand(-1.0, 1.0)  
		( \pan ++ i.asSymbol ).asSymbol.kr(rrand(-1,1.0))
	});

	delayPans = ( \refl_pan.kr(0) + ( \spread.kr(1) * delayPans) ).clip2(1);

	pannedReflections = Array.fill(numReflcs,
		{|i| 
			Pan2.ar(reflections.at(i), delayPans.at(i)) 
		}
	);


	Mix([
		pannedReflections.sum * \refl.kr( 0.8),
		input * \dry.kr(0.5)
	])

}).play
)



// Pseudo unit generator
// to be put in Platform.userExtensionDir ++ "/Reflector.sc"
Reflector : Object {
	*ar { arg
		input, 
		numReflcs = 5, 
		delayOffset = 0.02,
		scaleDelays = 1,
		spread = 1,
		reflPan = 0,
		lpfRefl = 0.9,
		hpfRefl = 150
		; 

		var primeDelays, delays, delayPans, reflections, pannedReflections;
		var filtered_input;

		primeDelays = [
			173, 179, 181, 191, 193, 197, 199, 211, 223, 227, 229,
			233, 239, 241, 251, 257, 263, 269, 271, 277, 281, 283,
			293, 307, 311, 313, 317, 331, 337, 347, 349, 353, 359,
		].scramble / 44100;

		numReflcs = numReflcs.clip(1,primeDelays.size);

		//form array of delay times
		delays = nil!numReflcs;

		delays[0] = delayOffset;

		(numReflcs - 1).do{arg i; 
			delays[i+1] = delays[i] + primeDelays[i];
		};

		delays = delays * scaleDelays;

		delayPans = Array.fill(numReflcs, {
			( reflPan + ( spread * Rand(-1.0, 1.0) ) ).clip2(1)
		});


		// sum to mono
		if(input.size > 1, {
			filtered_input = input.sum
		}, {
			filtered_input = input
		});	


		filtered_input = HPF.ar( 
			OnePole.ar( filtered_input, lpfRefl ), 
			hpfRefl 
		);


		reflections = Array.fill(numReflcs,
			{|i| 
				DelayN.ar(
					BPF.ar(
						filtered_input * (IRand(0,1) - 0.5 * 2 / (i+1 )), 
						freq: ExpRand(300, 2000), 
						// rq: 1 / (i+1) // every next reflection has  narrower spectrum
						rq: ExpRand(0.1,0.4)
					) ,
					0.2, 
					delays.at(i)
				) 
			}
		);

		pannedReflections = Array.fill(numReflcs,
			{|i| 
				Pan2.ar(reflections.at(i), delayPans.at(i)) 
			}
		);

		^pannedReflections.sum 

	}
}



(
SynthDef(\foo, { 
	var a,b,c,e;
	var freq, mFreq, mInd;

	freq = EnvGen.kr(Env.perc(Rand(0.001,0.004), Rand(0.001, 0.02 ))) *
	ExpRand(50,1000) + ExpRand(50,1000);

	// mFreq = freq 
	// *  2.pow( Rand(-5, 3) ) 
	// * EnvGen.kr(Env.perc(Rand(0.01,0.5), Rand(0.01,1.5))) 
	// + ( freq * (IRand(1,5)/IRand(1/5)) ) ;

	mFreq = 
	freq 
	*  2.pow( Rand(-1, 3) ) 
	* EnvGen.kr(Env.perc(ExpRand(0.001,0.005), Rand(0.001,0.005))) 
	+ 
	( freq * (IRand(1,5)/IRand(1,5)) ) ;

	mInd = freq 
	* 2.pow( Rand(-5, 1) ) 
	* EnvGen.kr(Env.perc(ExpRand(0.01,0.5), ExpRand(0.01,1.5))) 
	+ (freq * 2.pow( Rand(-5, 3) )) 
	;

	a = SinOsc.ar( freq + SinOsc.ar( mFreq, mul: mInd ) + Dust2.ar(500, freq * 0.5  ).lag(0.01));

	a = a * EnvGen.kr(Env.perc(
		ExpRand( 0.001,0.005 ), 
		TChoose.kr( 1, [5, ExpRand(0.01,0.05)] )
	)) * 0.3;

	a = LeakDC.ar(LPF.ar(a, 15000));
	
	a = Mix([ 
		Pan2.ar( a * Rand(0.4, 0.8), Rand(-1,1) ),
		Reflector.ar(
			input: a * 0.8, 
			numReflcs: 5, 
			delayOffset: 0.02, 
			scaleDelays: 1, 
			spread: 0.8, 
			reflPan: Rand(-1,1),
			lpfRefl: 0.1, 
			hpfRefl: 150
		)
	]);

	Out.ar( 0, a );

	DetectSilence.ar(a, doneAction: 2);

}).add;
)

( 
Routine {
    loop{
		Synth(\foo);
		rrand(0.01,1).wait;
		// 2.pow(rrand(-3,-2)).wait;
	}
}.play; 
)