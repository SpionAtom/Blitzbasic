Const xr = 640, yr = 640, fps = 60, D_DOWN = 1, D_RIGHT = 2, D_UP = 3, D_LEFT = 4
Global fps_timer
Global mo_x, mo_y, old_mo_x, old_mo_y, ms, level_timer, idc
Type Q
	Field id, x, y, w, h, r, g, b, gray
End Type

Graphics(xr, yr, 0, 2)
SetBuffer BackBuffer()
Local level = 0

Repeat
	level = level + 1
	Delete Each Q
	level_timer = MilliSecs()

	Select level
		Case 1:
			crQ(220, 220, 200, 200, 155, 155, 55)
			startCircle(320, 150, "Try to push all blocks out of the screen with your mouse.")
		Case 2:
			crQ(100, 220, 200, 200, 233, 233, 32)
			crQ(340, 220, 200, 200, 32, 233, 233)
			startCircle(320, 320, "Qs can only be moved if not blocked by other blocks.")
		Case 3:
			crQ(180, 180, 80, 80, 255, 155, 55)
			crQ(380, 180, 80, 80, 55, 255, 155)
			crQ(180, 380, 80, 80, 55, 155, 255)
			crQ(380, 380, 80, 80, 233, 233, 32)
			startCircle(320, 320, "Hit Space to restart.")
		Case 4:
			crQ(280, 280, 80, 80, 55, 55, 55, True)
			crQ(200, 280, 80, 80, 55, 255, 155)
			crQ(360, 280, 80, 80, 55, 155, 255)
			crQ(280, 200, 80, 80, 233, 233, 32)
			crQ(280, 360, 80, 80, 32, 233, 233)
			startCircle(200, 200, "Grey blocks are immovable.")
		Case 5:
			crQ(0, 40, 280, 80, 0, 155, 0)
			crQ(280, 40, 280, 80, 0, 155, 0)
			crQ(560, 40, 80, 520, 0, 155, 0)
			crQ(80, 560, 560, 80, 0, 155, 0)
			crQ(0, 120, 80, 520, 0, 155, 0)
			startCircle(320, 320, "Sometimes it's hard to see at first, where a block starts.")
		Case 6:
			For i = 0 To 5
				crQ(120 + i*80, 120, 40, 400, 255, 128, 0)
			Next
			For x = 0 To 4
				For y = 0 To 9
					crQ(160 + x*80, 120 + y*40, 40, 40, 255, 0, 128)
				Next
			Next
			startCircle(320, 20, "Slice and Dice")
		Case 7:
			i=0
			For x = 0 To 4
				For y = 0 To 4
					If (i Mod 2) = 0 
						crQ(120 + x*80, 120 + y*80, 80, 80, Rand(0,8)*64, Rand(0,8)*64, Rand(0,8)*64)
					Else
						crQ(120 + x*80, 120 + y*80, 80, 80, 55, 55, 55, True)
					EndIf
					i = i +1
				Next
			Next
			startCircle(320, 20, "Dance Party!")
		Default:
			Cls
			Color(255, 255, 0)
			Text(0, 0, "CONGRATULATIONS! YOU MASTERED ALL LEVELS!")
			Flip
			Repeat
			Until KeyDown(1) Or MouseDown(1) Or MouseDown(2)
			End
	End Select

	Repeat
		ms = MilliSecs()
		updateMouse

		If fps_timer + 1000 / fps < MilliSecs() 
			fps_timer = MilliSecs()
			Cls
			drawQs

			Select level
				Case 7:
					If ms > level_timer + 2500 
						level_timer = ms
						For b.Q = Each Q
							b\gray = b\gray Xor 1
							If b\gray 
								b\r = 55
								b\g = 55
								b\b = 55
							Else
								b\r = Rand(0,8)*64
								b\g = Rand(0,8)*64
								b\b = Rand(0,8)*64
							EndIf
						Next
					EndIf
			End Select

			Flip(0)
		End If
		If KeyHit(57)  level = level - 1: Exit
	Until KeyDown(1) Or checkWin()

Until KeyDown(1)

End




Function startCircle(x, y, txt$ = "")
	scr = 10
	Cls
	drawQs
	Color(255, 155, 55)
	Oval(x - scr, y - scr, 2 * scr, 2 * scr, 0)
	Text(0, 0, "Move mo into circle to start.")
	Text(0, 16, txt$)
	Flip
	Repeat
	Until KeyDown(1) Or Distanz(x, y, MouseX(), MouseY()) <= scr
End Function

Function crQ(x, y, w, h, r, g, b, gray = False)
	idc = idc + 1
	Q.Q = New Q
	Q\x = x
	Q\y = y
	Q\w = w
	Q\h = h
	Q\r = r
	Q\g = g
	Q\b = b
	Q\id = idc
	Q\gray = gray
End Function

Function drawQs()
	For Q.Q = Each Q
		Color(Q\r, Q\g, Q\b)
		Rect(Q\x, Q\y, Q\w, Q\h)
	Next
End Function

Function checkWin()
	notyet = False
	For cb.Q = Each Q
		If RectsOverlap(cb\x, cb\y, cb\w, cb\h, 2, 2, xr - 4, yr - 4) And cb\gray = False 
			notyet = True
		Else If cb\gray = False 
			Delete cb
		EndIf
	Next
	Return Not notyet
End Function

Function updateMouse()
	mo_x = MouseX()
	mo_y = MouseY()

	For cb.Q = Each Q
		If moInRect(cb\x, cb\y, cb\w, cb\h) And cb\gray = False 
			mx = cb\x + 0.5 * cb\w
			my = cb\y + 0.5 * cb\h

			If PointInTriangle(mo_x, mo_y, mx, my, cb\x, cb\y, cb\x + cb\w, cb\y) 
				moveQ cb, D_DOWN, mo_y - cb\y
			Else If PointInTriangle(mo_x, mo_y, mx, my, cb\x, cb\y, cb\x, cb\y + cb\h) 
				moveQ cb, D_RIGHT, mo_x - cb\x
			Else If PointInTriangle(mo_x, mo_y, mx, my, cb\x, cb\y + cb\h, cb\x + cb\w, cb\y + cb\h) 
				moveQ cb, D_UP, (cb\y + cb\h) - mo_y
			Else
				moveQ cb, D_LEFT, (cb\x + cb\w) - mo_x
			End If
		End If
	Next

	old_mo_x = mo_x
	old_mo_y = mo_y
End Function

Function moveQ(cb.Q, dir, length)
	For tb.Q = Each Q

		If cb\id <> tb\id 
			Select dir
				Case D_DOWN:
					If RectsOverlap(cb\x, cb\y + length, cb\w, cb\h, tb\x, tb\y, tb\w, tb\h)  MoveMouse old_mo_x, old_mo_y: Return
			Case D_RIGHT:
					If RectsOverlap(cb\x + length, cb\y, cb\w, cb\h, tb\x, tb\y, tb\w, tb\h)  MoveMouse old_mo_x, old_mo_y: Return
			Case D_UP:
					If RectsOverlap(cb\x, cb\y - length, cb\w, cb\h, tb\x, tb\y, tb\w, tb\h)  MoveMouse old_mo_x, old_mo_y: Return
			Case D_LEFT:
					If RectsOverlap(cb\x - length, cb\y, cb\w, cb\h, tb\x, tb\y, tb\w, tb\h)  MoveMouse old_mo_x, old_mo_y: Return
			End Select
		End If
	Next

	;move
	Select dir
		Case D_DOWN: cb\y = cb\y + length
		Case D_RIGHT: cb\x = cb\x + length
		Case D_UP: cb\y = cb\y - length
		Case D_LEFT: cb\x = cb\x - length
	End Select

End Function

Function moInRect(x, y, w, h)
	If mo_x <= x  Return False
	If mo_y <= y  Return False
	If mo_x >= x + w  Return False
	If mo_y >= y + h  Return False
	Return True
End Function

Function Winkel#(x1, y1, x2, y2)
	Return (360+ATan2(x1-x2,y1-y2)) Mod 360
End Function

Function Distanz#(x1, y1, x2, y2)
   Return Sqr((x1-x2)^2 + (y1-y2)^2)
End Function

Function sign# (p1x, p1y, p2x, p2y, p3x, p3y)
    Return (p1x - p3x) * (p2y - p3y) - (p2x - p3x) * (p1y - p3y);
End Function

Function PointInTriangle (px, py, v1x, v1y, v2x, v2y, v3x, v3y)
    b1 = sign(px, py, v1x, v1y, v2x, v2y) < 0.0
    b2 = sign(px, py, v2x, v2y, v3x, v3y) < 0.0
    b3 = sign(px, py, v3x, v3y, v1x, v1y) < 0.0
    Return (b1 = b2) And (b2 = b3)
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D