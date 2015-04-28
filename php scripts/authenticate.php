<?php
$con=mysqli_connect("mysql17.000webhost.com","a4376707_prii","prince123","a4376707_letcook");
if (mysqli_connect_errno($con))
{
   echo "Failed to connect to MySQL: " . mysqli_connect_error();
}
$username = $_POST['username'];
$password = $_POST['password'];
$password=$password;
$result = mysqli_query($con,"SELECT * FROM Users where 
Username='$username' and Password='$password'");
$row = mysqli_fetch_array($result);

if($row){
$data['username']=$row[0];
$data['favourites']=$row[2];
$data=json_encode($data);
echo $data;
}
else
echo 'incorrect';
mysqli_close($con);
?>