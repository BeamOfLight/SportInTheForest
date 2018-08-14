<?php
ini_set('display_errors', '1');
define('BASE_DIR', realpath(__DIR__));
require_once(BASE_DIR . '/config.php'); 

const REQUEST_CREATE_COMPETITION = "CreateCompetition";
const REQUEST_UPDATE_COMPETITION_INFO = "UpdateCompetitionInfo";
const REQUEST_GET_COMPETITION_INFO = "GetCompetitionInfo";
const REQUEST_INVITE_COMPETITION = "InviteCompetition";

function validate_isset_fields(array $fields)
{
	foreach ($fields as $field) {
		if (!isset($_POST[$field])) {
			die(json_encode(array('state' => 'error', 'msg' => 'empty_field_' . $field)));
		}
	}
}

function create_competition()
{
	$player_name = $_POST['player_name'];
    $exercise_name = $_POST['exercise_name'];
    $competition_state = $_POST['competition_state'];
    $competition_state_hash = $_POST['competition_state_hash'];
    $token = $_POST['token'];

	// Create connection
	$conn = new mysqli(DB_SERVERNAME, DB_USERNAME, DB_PWD, DB_NAME);
    mysqli_set_charset($conn,'utf8');
	// Check connection
	if (mysqli_connect_errno()) {
		var_dump(DB_SERVERNAME, DB_USERNAME, DB_PWD, DB_NAME);
		echo $conn->connect_error;
		die(json_encode(array('state' => 'error', 'msg' => 'connection_failed')));
	}
	$string_to_invite_code = strval(microtime()) . $token . $competition_state_hash . $exercise_name . $player_name;
	$invite_code = crc32($string_to_invite_code);

	/* создаем подготавливаемый запрос */
	$query = 'INSERT INTO competition (player_name, exercise_name, invite_code, competition_state, competition_state_hash, token) VALUES (?,?,?,?,?,?);';
	if ($stmt = $conn->prepare($query)) {

	    
	    $stmt->bind_param("ssssss", $player_name, $exercise_name, $invite_code, $competition_state, $competition_state_hash, $token);

	    /* запускаем запрос */
	    if ($stmt->execute()) {
	    	die(json_encode(array('state' => 'ok', 'invite_code' => $invite_code)));
	    } else {
	    	var_dump($result, $stmt->error);	
	    }

	    /* закрываем запрос */
	    $stmt->close();
	}
	$conn->close();
}

function update_competition_info()
{
	$invite_code = $_POST['invite_code'];
    $player_name = $_POST['player_name'];
    $exercise_name = $_POST['exercise_name'];
    $competition_state = $_POST['competition_state'];
    $token = $_POST['token'];

	// Create connection
	$conn = new mysqli(DB_SERVERNAME, DB_USERNAME, DB_PWD, DB_NAME);
    mysqli_set_charset($conn,'utf8');
	// Check connection
	if (mysqli_connect_errno()) {
		var_dump(DB_SERVERNAME, DB_USERNAME, DB_PWD, DB_NAME);
		echo $conn->connect_error;
		die(json_encode(array('state' => 'error', 'msg' => 'connection_failed')));
	}
	
	/* создаем подготавливаемый запрос */
	$query = 'UPDATE competition SET competition_state = ? WHERE invite_code = ? AND player_name = ? AND exercise_name = ? AND token = ?;';
	if ($stmt = $conn->prepare($query)) {
	    $stmt->bind_param("sssss", $competition_state, $invite_code, $player_name, $exercise_name, $token);

	    // /* запускаем запрос */
	    if ($stmt->execute()) {
	    	die(json_encode(array('state' => 'ok')));
	    } else {
	    	var_dump($result, $stmt->error);	
	    }

	    /* закрываем запрос */
	    $stmt->close();
	}
	$conn->close();
}

function get_competition_info()
{
	$invite_code = $_POST['invite_code'];
    $competition_state_hash = $_POST['competition_state_hash'];
    $token = $_POST['token'];

	// Create connection
	$conn = new mysqli(DB_SERVERNAME, DB_USERNAME, DB_PWD, DB_NAME);
    mysqli_set_charset($conn,'utf8');
	// Check connection
	if (mysqli_connect_errno()) {
		var_dump(DB_SERVERNAME, DB_USERNAME, DB_PWD, DB_NAME);
		echo $conn->connect_error;
		die(json_encode(array('state' => 'error', 'msg' => 'connection_failed')));
	}
	
	/* создаем подготавливаемый запрос */
	$query = 'SELECT competition_state FROM competition WHERE invite_code = ?;';
	if ($stmt = $conn->prepare($query)) {
	    $stmt->bind_param("s", $invite_code);

		// $row = $result->fetch_array(MYSQLI_ASSOC);
		// var_dump($row);
	    // /* запускаем запрос */
	    if ($stmt->execute()) {
	    	$result = $stmt->get_result();
	    	//var_dump($result->fetch_array(MYSQLI_ASSOC));
	    	$data = $result->fetch_array(MYSQLI_ASSOC);
	    	$competition_state =isset($data['competition_state']) ? $data['competition_state'] : '';
	    	die(json_encode(array('state' => 'ok', 'competition_state' => $competition_state)));
	    } else {
	    	var_dump($result, $stmt->error);	
	    }

	    /* закрываем запрос */
	    $stmt->close();
	}
	$conn->close();
}

if (isset($_POST["request"])) {
	$request = $_POST["request"];
	switch($request)
	{
		case REQUEST_CREATE_COMPETITION:
			validate_isset_fields(array("token", "player_name", "exercise_name", "competition_state", 'competition_state_hash'));
			create_competition();
			break;
		case REQUEST_UPDATE_COMPETITION_INFO:
			validate_isset_fields(array("token", "player_name", "exercise_name", 'competition_state', 'invite_code'));
			update_competition_info();
			break;
		case REQUEST_GET_COMPETITION_INFO:
			validate_isset_fields(array("token", "competition_state_hash", "invite_code"));
			get_competition_info();
			break;
		//case REQUEST_INVITE_COMPETITION:
		//	validate_isset_fields(array("token", "player_name", "invite_code"));
		//	echo "4";
		//	break;
		default:
			die(json_encode(array('state' => 'error', 'msg' => 'unknown_command')));
			break;
	}
} else {
	die(json_encode(array('state' => 'error', 'msg' => 'bad_request')));
}

?>