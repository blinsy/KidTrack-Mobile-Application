<?php
$data = file_get_contents("php://input");
if($data!=''){
$infomation=json_decode($data,true);
header("Content-Type: application/json; charset=UTF-8");
require '../sessions/functions.php';
echo json_encode($HasofGroup->viewmykid($infomation),JSON_PRETTY_PRINT);
}else{
echo 'error body cannot be null';
}


?>