# File generated by Workcraft.
.inputs iface_b iface_a iface_c
.outputs iface_q
.dummy d1 d2 d3 d4
.graph
p6 iface_b+/1
iface_b+/1 p9
p5 iface_a+/1
iface_a+/1 p8
p34 iface_b-/2
iface_b-/2 p20
p43 iface_q-/3
iface_q-/3 p44
p38 iface_c-/3
iface_c-/3 p23
p39 iface_q-/2
iface_q-/2 p40
p41 iface_a-/3
iface_a-/3 p24
p22 d3
p23 d3
p40 d3
d3 p4
p24 d4
p25 d4
p44 d4
d4 p4
p1 iface_b-/1
iface_b-/1 p37
iface_b-/1 p38
iface_b-/1 p39
p37 iface_a-/2
iface_a-/2 p22
p1 iface_c-/1
iface_c-/1 p41
iface_c-/1 p42
iface_c-/1 p43
p20 d2
p21 d2
p36 d2
d2 p4
p1 iface_a-/1
iface_a-/1 p11
iface_a-/1 p34
iface_a-/1 p35
p4 d1
d1 p5
d1 p6
d1 p7
p42 iface_b-/3
iface_b-/3 p25
p35 iface_c-/2
iface_c-/2 p21
p11 iface_q-/1
iface_q-/1 p36
p8 iface_q+/1
p9 iface_q+/1
p10 iface_q+/1
iface_q+/1 p1
p7 iface_c+/1
iface_c+/1 p10
.marking { p4 }
.end
