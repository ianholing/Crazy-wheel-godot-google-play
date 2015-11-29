
extends Node2D

# member variables here, example:
var circleRotation = 0
var circleVelocity = 0.03
var arrowRotation = 0
var arrowVelocity = 0.05
var arrowAcceleration = 0.001
var arrowDirection = 1

var score = 0
var circlePos = 0
var arrowRelPos = 0
var overColor = false
var oldOverColor = false

var startCircleRotation = 0
var startCircleVelocity = 0.03
var startArrowRotation = 0
var startArrowVelocity = 0.05
var startArrowAcceleration = 0.001
var startArrowDirection = 1

var circle
var arrow
var startButton
var arrowFrame = 3
var mouse_down = false

var playing = false

func _ready():
	circle = get_node("Circle")
	arrow = get_node("Arrow")
	startButton = get_node("StartButton")
	arrow.hide()
	arrow.set_frame(arrowFrame)
	set_process(true)
	set_process_input(true)

func _process(delta):
	circleRotation -= circleVelocity
	circle.set_rot(circleRotation)
	if (playing):
		arrowRotation +=  arrowVelocity * arrowDirection
		arrow.set_rot(arrowRotation)
		calculate_rotations()
			
		# You cannot pass the color
		oldOverColor = overColor
		if (arrowRelPos  > circlePos + 45 || arrowRelPos < circlePos - 45):
			if (overColor):
				print ("OUCH! YOU PASS THE COLOR :(")
				game_over()
			overColor = false
		else:
			overColor = true
			
		updateHID()
	
func _input(ev):
	if (playing):
		if (ev.type == InputEvent.MOUSE_BUTTON):
			if (mouse_down):
				mouse_down = false
				if (arrowRelPos  > circlePos + 45 || arrowRelPos < circlePos - 45):
					print ("CLICK OUT THE ZONE")
					game_over()
				else:
					overColor = false
					score += 1
					print ("AWESOME! CLICK IN THE ZONE, SCORE: " + str(score))
				arrowDirection = -arrowDirection
				var oldArrowFrame = arrowFrame
				while (arrowFrame == oldArrowFrame):
					randomize()
					arrowFrame = randi() % 4
				arrow.set_frame(arrowFrame)
			else:
				mouse_down = true

func _on_ChangeTimer_timeout():
	if (playing):
		arrowVelocity += arrowAcceleration

func _on_TextureButton_pressed():
	start_game()
	
func calculate_rotations():
	# Calculate each frame the relative position in degrees of circle and arrow
	circlePos = (int(rad2deg(circleRotation) / 360) * 360 - rad2deg(circleRotation));
	arrowRelPos = (int((rad2deg(arrowRotation) + 90*arrowFrame) / 360) * 360 - (rad2deg(arrowRotation) + 90*arrowFrame));
	if (arrowRelPos < 0):
		arrowRelPos = 360 + arrowRelPos
	if (circlePos < 0):
		circlePos = 360 + circlePos
	
	# Normalize both positions to allow comparations
	if (arrowRelPos - circlePos > 315):
		circlePos += 360
	if (circlePos - arrowRelPos > 315):
		arrowRelPos += 360
	
func updateHID():
	get_node("OldOverColor").set_text(str(oldOverColor))
	get_node("OverColor").set_text(str(overColor))
	get_node("ArrowRelPos").set_text(str(arrowRelPos))
	get_node("CirclePos").set_text(str(circlePos))
	get_node("Score").set_text("Score: " + str(score))
	
func start_game():
	circleRotation = startCircleRotation
	circleVelocity = startCircleVelocity
	arrowRotation = startArrowRotation
	arrowVelocity = startArrowVelocity
	arrowAcceleration = startArrowAcceleration
	arrowDirection = startArrowDirection
	score = 0
	get_node("OldOverColor").show()
	get_node("OverColor").show()
	get_node("ArrowRelPos").show()
	get_node("CirclePos").show()
	get_node("Score").show()
	startButton.hide()
	arrow.show()
	playing = true
	
func game_over():
	playing = false
	#get_node("OldOverColor").hide()
	#get_node("OverColor").hide()
	#get_node("ArrowRelPos").hide()
	#get_node("CirclePos").hide()
	#get_node("Score").hide()
	arrow.hide()
	startButton.show()
	print ("GAME OVER")
	print ("YOU DID IT GREAT, SCORE: " + str(score))