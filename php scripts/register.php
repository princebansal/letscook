<?php
$con=mysqli_connect("mysql17.000webhost.com","a4376707_prii","prince123","a4376707_letcook");
if (mysqli_connect_errno($con))
{
   echo "Failed to connect to MySQL: " . mysqli_connect_error();
}
$username = $_GET['username'];
$password = $_GET['password'];
$result = mysqli_query($con,"SELECT * FROM Users where Username='$username';");
$row = mysqli_fetch_array($result);

if($row){
    echo "exist";
}
else{
    $res=mysqli_query($con,"INSERT INTO Users values('$username','$password',\"none\");");
    if($res)
    echo 'successful';
    else
        echo 'error';
}
mysqli_close($con);
?>