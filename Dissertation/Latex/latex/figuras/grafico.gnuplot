set dummy t
set size 1,0.5
set samples 400
set xrange [0:20]
set format y "%4.1f"
set xtics border nomirror norotate 0,2,20
set mxtics 2
set xzeroaxis
set xlabel
set yrange [-1:5]
set ytics border nomirror norotate -1,1,5
set nomytics
set noyzeroaxis
set nogrid
set origin 0,0
set ticslevel 0
set ylabel
set terminal postscript eps monochrome dashed "Helvetica" 10
set output "grafico.eps"
plot [t=0:20] \
wn = 1, ksi = 0.2, \
T = 0, \
y(t) = 1 - exp(-ksi*wn*t)*(cos(wn*sqrt(1-ksi*ksi)*t)+((ksi-T*wn)/sqrt(1-ksi*ksi))*sin(wn*sqrt(1-ksi*ksi)*t)), \
y(t) title "T= 0.00 ->  sem zero", \
T = 1/(10*ksi*wn), \
y(t) = 1 - exp(-ksi*wn*t)*(cos(wn*sqrt(1-ksi*ksi)*t)+((ksi-T*wn)/sqrt(1-ksi*ksi))*sin(wn*sqrt(1-ksi*ksi)*t)), \
y(t) title "T= 0.50 -> z=10Re(p)", \
T = 1/(5*ksi*wn), \
y(t) = 1 - exp(-ksi*wn*t)*(cos(wn*sqrt(1-ksi*ksi)*t)+((ksi-T*wn)/sqrt(1-ksi*ksi))*sin(wn*sqrt(1-ksi*ksi)*t)), \
y(t) title "T= 1.00 -> z= 5Re(p)", \
T = 1/(ksi*wn), \
y(t) = 1 - exp(-ksi*wn*t)*(cos(wn*sqrt(1-ksi*ksi)*t)+((ksi-T*wn)/sqrt(1-ksi*ksi))*sin(wn*sqrt(1-ksi*ksi)*t)), \
y(t) title "T= 5.00 -> z=  Re(p)", \
T = -1/(5*ksi*wn), \
y(t) = 1 - exp(-ksi*wn*t)*(cos(wn*sqrt(1-ksi*ksi)*t)+((ksi-T*wn)/sqrt(1-ksi*ksi))*sin(wn*sqrt(1-ksi*ksi)*t)), \
y(t) title "T=-1.00 -> z=-5Re(p)"
