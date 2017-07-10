;Angle & Distance



;Calculate distance between two points
Function distanceQ(x1, y1, x2, y2)
	Return ((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2))
End Function


;Calculate the angle between X-Axis and a line given by two points
Function Winkel#(x1#,y1#,x2#,y2#)
 Return (360+ATan2(x1#-x2#,y1#-y2#)) Mod 360
End Function
;~IDEal Editor Parameters:
;~C#Blitz3D