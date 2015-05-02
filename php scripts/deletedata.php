<?php
$con=mysqli_connect("mysql17.000webhost.com","a4376707_prii","prince123","a4376707_letcook");
if (mysqli_connect_errno($con))
{
   echo "Failed to connect to MySQL: " . mysqli_connect_error();
}
$id = $_GET['id'];
$id=intval($id);
$type=$_GET['type'];
$type=intval($type);
$typel="";
if($type==1)
    $typel="chefs";
else
    $typel="recipes";
/*$result = mysqli_query($con,"SELECT * FROM '$typel' where id='$id';");
while($row = mysqli_fetch_array($result)){
*/
    $result=mysqli_query($con,"DELETE FROM $typel where id='$id';");
    if($result)
    echo "successful";
//}
mysqli_close($con);
?>