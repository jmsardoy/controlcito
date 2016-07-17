from pykeyboard import PyKeyboard

k = PyKeyboard()

platforms = ["VLC", "POPCORN TIME", "YOUTUBE", "NETFLIX"]
actions = ["PLAY","VOLUP","VOLDOWN","FORWARD","REWIND"]

vlc = [[(k.tap_key,k.space)],
	[(k.press_key,k.control_key),(k.tap_key,k.up_key),(k.release_key,k.control_key)],
	[(k.press_key,k.control_key),(k.tap_key,k.down_key),(k.release_key,k.control_key)],
	[(k.press_key,k.shift_key),(k.tap_key,k.right_key),(k.release_key,k.shift_key)],
	[(k.press_key,k.shift_key),(k.tap_key,k.left_key),(k.release_key,k.shift_key)]]

popcorn_time = [[(k.tap_key,k.space)],
	[(k.tap_key,k.up_key)],
	[(k.tap_key,k.down_key)],
	[(k.tap_key,k.right_key)],
	[(k.tap_key,k.left_key)]]

youtube = [[(k.tap_key,"k")],
	[(k.tap_key,k.up_key)],
	[(k.tap_key,k.down_key)],
	[(k.tap_key,"l")],
	[(k.tap_key,"j")]]

netflix = [[(k.tap_key,k.space)],
	[(k.tap_key,k.up_key)],
	[(k.tap_key,k.down_key)],
	[(k.tap_key,k.right_key),(k.tap_key,k.space)],
	[(k.tap_key,k.left_key),(k.tap_key,k.space)]]

platforms_keys = [vlc,popcorn_time,youtube,netflix]

def keyboard_action(action,platform):
	action = action.upper()
	platform = platform.upper()
	if action in actions and platform in platforms:
		action_index = actions.index(action)
		platform_index = platforms.index(platform)
		platform_keys = platforms_keys[platform_index]
		for function,key in platform_keys[action_index]:
			function(key)
