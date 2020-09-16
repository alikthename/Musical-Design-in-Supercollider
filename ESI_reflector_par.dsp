declare name "ESI_Reflector";
declare author "Alik Rustamoff";

import("stdfaust.lib");

nRefl = 5;
cc(x) =  hgroup( "Common Controls", x);
scaleDelays =  cc( hslider("[0]Scale Delays",1,0,3,0.001));
//scaleWidth =  cc( hslider("[0]Scale Width",1,0,3,0.001));
//biasPans =  cc( hslider("[0] Bias Pans",1,0,3,0.001));

reflector(N) = par(i,N, de.delay(100000, dt(i))): par(i,N, fi.resonbp(fc(i),Q(i),level(i))) : par(i, N, sp.panner(g(i)) )
with {
	
        //a = j+1; // just so that band numbers don't start at 0
      	level(j) = hslider("[0]Level %j",0,-1,1,0.01) : si.smoo;
        fc(j) = hslider("[2]Freq %j",2000, 20, 20000, 0.01) : si.smoo;
        Q(j) = hslider("[3]Q %j",1,0.01,10,0.01) : si.smoo;
        dt(j) = hslider("[1] Delay %j [unit:ms]",20, 20,150,1) * 44.1 * scaleDelays : si.smoo;
        g(j) = hslider("[4] Pan %j",0.5, 0,1,0.001) : si.smoo;

};


process(x,y) = x+y : fi.highpass(1,hpf) : fi.lowpass(1,lpf) <: reflector(nRefl) :> (_ * dw) + (x * (1-dw)), (_ * dw) + (y * (1-dw)):_*gain,_*gain
with{
lpf = cc( hslider("[2] Lpf [unit:Hz]", 5000, 50, 20000, 1) ) : si.smoo;
hpf = cc( hslider("[1] Hpf [unit:Hz]", 50, 50, 1600, 1) ) : si.smoo;
dw = cc( vslider("[3] Dry-Wet",0.5, 0,1,0.001) );
gain = hslider("[4] Output Gain",0,-20,20,0.01) : ba.db2linear;

};

