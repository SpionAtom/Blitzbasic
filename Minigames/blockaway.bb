Const xr = 640, yr = 640, fps = 60, D_DOWN = 1, D_RIGHT = 2, D_UP = 3, D_LEFT = 4
Global fps_timer = MilliSecs()
Global mouse_x, mouse_y, old_mouse_x, old_mouse_y, ms, level_timer
Type Block
	Field id, x, y, w, h, r, g, b, gray
End Type
Global Block.Block, idcounter = 0


Graphics(xr, yr, 0, 2)
SetBuffer BackBuffer()
Local level = 0

Repeat
	level = level + 1
	Delete Each Block
	level_timer = MilliSecs()

	Select level
		Case 1:
			createBlock(220, 220, 200, 200, 155, 155, 55)
			startCircle(320, 150, "Try to push all blocks out of the screen with your mouse.")
		Case 2:
			createBlock(100, 220, 200, 200, 233, 233, 32)
			createBlock(340, 220, 200, 200, 32, 233, 233)
			startCircle(320, 320, "Blocks can only be moved if not blocked by other blocks.")
		Case 3:
			createBlock(180, 180, 80, 80, 255, 155, 55)
			createBlock(380, 180, 80, 80, 55, 255, 155)
			createBlock(180, 380, 80, 80, 55, 155, 255)
			createBlock(380, 380, 80, 80, 233, 233, 32)
			startCircle(320, 320, "Hit Space to restart.")
		Case 4:
			createBlock(280, 280, 80, 80, 55, 55, 55, True)
			createBlock(200, 280, 80, 80, 55, 255, 155)
			createBlock(360, 280, 80, 80, 55, 155, 255)
			createBlock(280, 200, 80, 80, 233, 233, 32)
			createBlock(280, 360, 80, 80, 32, 233, 233)
			startCircle(200, 200, "Grey blocks are immovable.")
		Case 5:
			createBlock(0, 40, 280, 80, 0, 155, 0)
			createBlock(280, 40, 280, 80, 0, 155, 0)
			createBlock(560, 40, 80, 520, 0, 155, 0)
			createBlock(80, 560, 560, 80, 0, 155, 0)
			createBlock(0, 120, 80, 520, 0, 155, 0)
			startCircle(320, 320, "Sometimes it's hard to see at first, where a block starts.")
		Case 6:
			for i = 0 to 5
				createBlock(120 + i*80, 120, 40, 400, 255, 128, 0)
			next
			for x = 0 to 4
				for y = 0 to 9
					createBlock(160 + x*80, 120 + y*40, 40, 40, 255, 0, 128)
				next
			next
			startCircle(320, 20, "Slice and Dice")
		Case 7:
			i=0
			for x = 0 to 4
				for y = 0 to 4
					If (i mod 2) = 0 Then
						createBlock(120 + x*80, 120 + y*80, 80, 80, rand(0,8)*64, rand(0,8)*64, rand(0,8)*64)
					Else
						createBlock(120 + x*80, 120 + y*80, 80, 80, 55, 55, 55, true)
					Endif
					i = i +1
				next
			next
			startCircle(320, 20, "Dance Party!")
		Default:
			Cls
			Color(255, 255, 0)
			Text(0, 0, "CONGRATULATIONS! YOU MASTERED ALL LEVELS!")
			Flip()
			Repeat
			Until KeyDown(1) Or MouseDown(1) Or MouseDown(2)
			End
	End Select

	Repeat
		ms = MilliSecs()
		updateMouse()

		If fps_timer + 1000 / fps < MilliSecs() Then
			fps_timer = MilliSecs()
			Cls
			drawBlocks()

			Select level
				Case 7:
					If ms > level_timer + 2500 Then
						level_timer = ms
						For b.Block = Each Block
							b\gray = b\gray xor 1
							If b\gray Then
								b\r = 55
								b\g = 55
								b\b = 55
							Else
								b\r = rand(0,8)*64
								b\g = rand(0,8)*64
								b\b = rand(0,8)*64
							Endif
						Next
					Endif
			End Select

			Flip(0)
		End If
		If KeyHit(57) Then level = level - 1: Exit
	Until KeyDown(1) Or checkWin()

Until KeyDown(1)

End




Function startCircle(x, y, txt$ = "")
	scr = 10
	Cls
	drawBlocks()
	Color(255, 155, 55)
	Oval(x - scr, y - scr, 2 * scr, 2 * scr, 0)
	Text(0, 0, "Move mouse into circle to start.")
	Text(0, 16, txt$)
	Flip()
	Repeat
	Until KeyDown(1) Or Distanz(x, y, MouseX(), MouseY()) <= scr
End Function

Function createBlock(x, y, w, h, r, g, b, gray = False)
	idcounter = idcounter + 1
	Block.Block = New Block
	Block\x = x
	Block\y = y
	Block\w = w
	Block\h = h
	Block\r = r
	Block\g = g
	Block\b = b
	Block\id = idcounter
	Block\gray = gray
End Function

Function drawBlocks()
	For Block = Each Block
		Color(Block\r, Block\g, Block\b)
		Rect(Block\x, Block\y, Block\w, Block\h)
	Next
End Function

Function checkWin()
	;won, if all blocks out of screen
	notyet = False
	For cb.Block = Each Block
		If RectsOverlap(cb\x, cb\y, cb\w, cb\h, 2, 2, xr - 4, yr - 4) And cb\gray = False Then
			notyet = True
		Else If cb\gray = False Then
			Delete cb
		Endif
	Next
	;won
	Return Not notyet
End Function

Function updateMouse()
	mouse_x = MouseX()
	mouse_y = MouseY()

	For cb.Block = Each Block
		If mouseInRect(cb\x, cb\y, cb\w, cb\h) And cb\gray = False Then
			mx = cb\x + 0.5 * cb\w
			my = cb\y + 0.5 * cb\h

			If PointInTriangle(mouse_x, mouse_y, mx, my, cb\x, cb\y, cb\x + cb\w, cb\y) Then ;move block down
				moveBlock cb, D_DOWN, mouse_y - cb\y
			Else If PointInTriangle(mouse_x, mouse_y, mx, my, cb\x, cb\y, cb\x, cb\y + cb\h) Then ;move block right
				moveBlock cb, D_RIGHT, mouse_x - cb\x
			Else If PointInTriangle(mouse_x, mouse_y, mx, my, cb\x, cb\y + cb\h, cb\x + cb\w, cb\y + cb\h) Then ;move block up
				moveBlock cb, D_UP, (cb\y + cb\h) - mouse_y
			Else ;move block left
				moveBlock cb, D_LEFT, (cb\x + cb\w) - mouse_x
			End If
		End If
	Next

	old_mouse_x = mouse_x
	old_mouse_y = mouse_y
End Function

Function moveBlock(cb.Block, dir, length)
DebugLog dir + ", " + length
	For tb.Block = Each Block

		If cb\id <> tb\id Then ;check if collide with other block
			Select dir
				Case D_DOWN:
					If RectsOverlap(cb\x, cb\y + length, cb\w, cb\h, tb\x, tb\y, tb\w, tb\h) Then
						DebugLog "Overlap"
						MoveMouse old_mouse_x, old_mouse_y: Return
					End If
			Case D_RIGHT:
					If RectsOverlap(cb\x + length, cb\y, cb\w, cb\h, tb\x, tb\y, tb\w, tb\h) Then
						MoveMouse old_mouse_x, old_mouse_y: Return
						DebugLog "Overlap"
					End If
			Case D_UP:
					If RectsOverlap(cb\x, cb\y - length, cb\w, cb\h, tb\x, tb\y, tb\w, tb\h) Then
						MoveMouse old_mouse_x, old_mouse_y: Return
						DebugLog "Overlap"
					End If
			Case D_LEFT:
					If RectsOverlap(cb\x - length, cb\y, cb\w, cb\h, tb\x, tb\y, tb\w, tb\h) Then
						MoveMouse old_mouse_x, old_mouse_y: Return
						DebugLog "Overlap"
					End If
			End Select
		End If
	Next

	;move after no collision is detected
	Select dir
		Case D_DOWN: cb\y = cb\y + length
		Case D_RIGHT: cb\x = cb\x + length
		Case D_UP: cb\y = cb\y - length
		Case D_LEFT: cb\x = cb\x - length
	End Select

End Function

;Prüft, ob sich die Maus in einem angegebenen Rechteck befindet
Function mouseInRect(x, y, w, h)
	If mouse_x <= x Then Return False
	If mouse_y <= y Then Return False
	If mouse_x >= x + w Then Return False
	If mouse_y >= y + h Then Return False
	Return True
End Function


;Berechnet den Winkel zwischen der X-Achse und einer Geraden durch zwei Punkte
Function Winkel#(x1#,y1#,x2#,y2#)
	Return (360+ATan2(x1#-x2#,y1#-y2#)) Mod 360
End Function

;Berechnet den Abstand zwischen zwei Punkten
Function Distanz#(x1#, y1#, x2#, y2#)
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
