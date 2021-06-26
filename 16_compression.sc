

Ndef( \compressor).clear;
Ndef( \compressor).ar(2);

(

Ndef( \compressor, {
	var local;
	var attack = \attack.kr(0.01);
	var release = \release.kr(0.1);
	var dry, drywet, in, t, o, r, c, e, lookahead;
	var kneelag;
	var bias;
	
	t = \threshhold.kr(-6);
	
	dry = SoundIn.ar([0,1]);
	
	in = dry * \trim.kr(0).dbamp;

	e = in.mean;
	
	e = HPF.ar( e, \hpf.kr(50) );

	e = EnvDetect.ar(e, attack, release);
	// e = e.abs.lagud(attack, release);
	
	// how much we are over by
	o = e.ampdb.excess(t);
	
	// scale the excess value by ratio
	r = \ratio.kr(4);
	c = ( max(o, 0.0) ) * (r.reciprocal - 1);

	kneelag = attack * \knee.kr(0.0);

	c = c.lag( kneelag );
	c = c.dbamp;
	
	lookahead = \lookahead.kr(0);
	in = DelayC.ar( in, 0.5, ( attack + release + kneelag * lookahead).lag(0.4) ); // lookahead
	in = in * c;
	in = in	* \gain.kr(0).dbamp;
	bias = K2A.ar(\bias.kr(0.0));

	in = Select.ar( \saturate.kr(1), [in, (in + bias).softclip - bias] );
	in = LeakDC.ar(in);

	drywet = \dry_wet.kr(1);
	Mix([
		in * drywet,
		DelayC.ar( dry * (1 - drywet), 0.5, ( attack + release + kneelag * lookahead).lag(0.4) )
	])
	

} ).play;


Ndef(\compressor).addSpec(
	\attack, [0.0000001,0.1, \exp],
	\release, [0.0000001,0.4, \exp],
	\threshhold, [0,-120],
	\trim, [0,60],
	\gain, [0,60],
	\ratio, [1,20, \exp],
	\lookahead, [0.0,1],
	\saturate, \switch,
	\dry_wet, [0,1],
	\hpf, [10, 1000] ,
	\knee, [0.0, 10] ,
	\bias, [0.0, 0.5] ,
);

)

Ndef( \compressor).edit;



// default
Ndef('compressor').set('ratio', 1.0, 'threshhold', 0.0, 'release', 0.07387504697919, 'attack', 0.008961505019466, 'hpf', 10.0, 'dry_wet', 0.0);

// hard
Ndef('compressor').set('ratio', 3.0071311728225, 'threshhold', -41.269841269841, 'trim', 0.0, 'gain', 26.455026455026, 'release', 0.042070437624253, 'attack', 0.0037275937203149, 'hpf', 10.0, 'dry_wet', 1.0);

// aggr
Ndef('compressor').set('ratio', 3.0071311728225, 'threshhold', -34.920634920635, 'trim', 0.0, 'gain', 26.455026455026, 'release', 0.049412872163151, 'attack', 0.005372281118324, 'hpf', 10.0, 'dry_wet', 1.0);


// clicks
Ndef('compressor').set('ratio', 10.449735449735, 'threshhold', -74.920634920635, 'gain', 35.978835978836, 'release', 0.030496562696818, 'attack', 0.035938136638046);



({
var a = SinOsc.ar(2).abs;
a
}.plot(1))


({
var a = SinOsc.ar(2).abs;
a.ampdb
}.plot(1))


[ 0.1, 0.01, 0.001, 0.0001, 0.00001, 0.000001, 0.0000001, 0.0  ].ampdb


({
var a = SinOsc.ar(2).abs;
a.excess(0.7)
// same as
// a - a.clip(0,0.7) 
}.plot(1))


({
var a = SinOsc.ar(2).abs;
a = a.ampdb;
a.excess(-20)
}.plot(1))

({ 
var a = SinOsc.ar(2).abs;
var ratio = 2;
a =  a.ampdb;
a = a.excess(-20);
a = max(a, 0.0);
a = a * (ratio.reciprocal - 1);
a = a.lag(0.01);
a = a.dbamp
}.plot(1))


// as the ratio approaches inf we get closer to 1
[ 2, 4, 8, 20, 40, 60, 80, 100, 120 ].reciprocal - 1

