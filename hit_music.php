<?php
$con = mysqli_connect("localhost", "icd0422", "icando0422*", "icd0422");
$result = mysqli_query($con, "SELECT * FROM HIT_MUSIC;") ;
$response = array() ;

while($row = mysqli_fetch_array($result))
{
    array_push($response, array("title" => $row[1], "singer" => $row[2], "high_octave" => $row[3], "link" => $row[4]));
}

echo json_encode(array("response"=>$response)) ;
mysqli_close($con) ;
?>