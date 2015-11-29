
extends Node2D

# member variables here, example:
var circleRotation = 0;
var circleVelocity = 0.03;
var arrowRotation = 0;
var arrowVelocity = 0.05;
var arrowAcceleration = 0.001;

var arrowDirection = 1;
var circle;
var arrow; 
var arrowFrame = 3;
var mouse_down = false;

func _ready():
	circle = self.get_node("Circle")
	arrow = self.get_node("Arrow")
	set_process(true)
	set_process_input(true)
	pass

func _process(delta):
	circleRotation -= circleVelocity
	arrowRotation +=  arrowVelocity * arrowDirection
	circle.set_rot(circleRotation)
	arrow.set_rot(arrowRotation)
	pass
	
func _input(ev):
	if (ev.type==InputEvent.MOUSE_BUTTON):
		if (mouse_down):
			mouse_down = false
			arrowDirection = -arrowDirection
			var oldArrowFrame = arrowFrame
			while (arrowFrame == oldArrowFrame):
				randomize()
				arrowFrame = randi() % 4
				print ("ARROW FRAME: ", arrowFrame)
			arrow.set_frame(arrowFrame)
		else:
			mouse_down = true
	pass

func _on_ChangeTimer_timeout():
	arrowVelocity += arrowAcceleration
	pass
