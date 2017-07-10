;Schiebepuzzle 2 - SpionAtom - Juli 2o17
Const xr = 640, yr = 640
Graphics xr, yr, 0, 2
Global mouse_x, mouse_y

	Const mapw = 5, maph = 5
	Dim map(mapw, maph)
	Global emptyX, emptyY
	Global mapg = xr / mapw
	Global mapx = (xr - mapw * mapg) / 2
	Global mapy = (yr - maph * mapg) / 2
	
	
	SetFont LoadFont("Impact", mapg * 0.8)
	Global imgMap = createMapImage()
	SetFont LoadFont("Courier New", 16)
	resetMap()
	
	SetBuffer BackBuffer()
	Repeat
		mouse_x = MouseX()
		mouse_y = MouseY()
		in_x = (mouse_x - mapx) / mapg
		in_y = (mouse_y - mapy) / mapg
		
		If KeyHit(57) ;Leertaste Feld reset
			resetMap
		End If
		
		If MouseHit(2) Then ;Feld mischen
			scrambleMap
			steps = 0
			AppTitle "Steps: " + steps
		End If		
		
		;Wenn Maus im Feld ist
		If mouseInRect(mapx, mapy, mapw * mapg, maph * mapg) Then		
			If MouseHit(1) Then
				If in_x = emptyX Xor in_y = emptyY Then
					steps = steps + 1
					AppTitle "Steps: " + steps
					moveFieldAt(in_x, in_y)
				End If
			End If
		End If	
		
	
		Cls
			drawMap
		Flip()
		
	Until KeyDown(1)
	End
	
	
Function moveFieldAt(x, y)		

	If emptyY = y Then	
		s = Sgn(x - emptyX)	
		k = emptyX
		While k <> x
			map(k, y) = map(k + s, y)
			k = k + s
		Wend
		emptyX = x
	
	End If

	If emptyX = x Then	
		s = Sgn(y - emptyY)	
		k = emptyY
		While k <> y
			map(x, k) = map(x, k + s)
			k = k + s
		Wend
		emptyY = y	
	End If
	
	map(emptyX, emptyY) = 0	
	
End Function

Function resetMap()

	n = 1
	For y = 0 To maph - 1
		For x = 0 To mapw - 1	
			map(x, y) = n
			n = n + 1
		Next
	Next
	emptyX = mapw - 1
	emptyY = maph - 1
	map(emptyX, emptyY) = 0

End Function

Function scrambleMap(times = 1000)

	Local inX, inY
	For i = 1 To times
		inX = emptyX
		inY = emptyY
		If Rand(2) = 1 Then
			Repeat: inX = Rand(0, mapw - 1): Until inX <> emptyX
		Else
			Repeat: inY = Rand(0, maph - 1): Until inY <> emptyY
		End If
		moveFieldAt(inX, inY)
		
	Next

End Function

Function createMapImage(file$ = "", showNumbers = True)

	Local img = CreateImage(mapw * mapg, maph * mapg)
	SetBuffer ImageBuffer(img)

	If file$ <> "" Then
		imgFile = LoadImage(file)
		ResizeImage imgFile, mapw * mapg, maph * mapg
		DrawBlock imgFile, 0, 0
	End If
	
	If Not showNumbers Then Return img
	
	n = 1
	For y = 0 To maph - 1
	For x = 0 To mapw - 1
		Rect x * mapg + 1, y * mapg + 1, mapg - 2, mapg - 2, 0
		t$ = Str(n)
		Text x * mapg + (mapg - StringWidth(t)) / 2, y * mapg + (mapg - StringHeight(t)) / 2, t
		n = n + 1
	Next
	Next
	
	Return img

End Function

Function drawMap()

	For j = 0 To maph - 1
	For i = 0 To mapw - 1
		If map(i, j) > 0 Then
			x = (map(i, j) - 1) Mod mapw
			y = (map(i, j) - 1) / mapw
			DrawImageRect imgMap, mapx + i * mapg, mapy + j * mapg, x * mapg, y * mapg, mapg, mapg	
		End If
	Next
	Next

End Function

Function mouseInRect(x, y, w, h)

	If mouse_x <= x Then Return False
	If mouse_y <= y Then Return False
	If mouse_x >= x + w Then Return False
	If mouse_y >= y + h Then Return False
	Return True	

End Function
;~IDEal Editor Parameters:
;~C#Blitz3D