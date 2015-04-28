<?php
$con=mysqli_connect("mysql17.000webhost.com","a4376707_prii","prince123","a4376707_letcook");
if (mysqli_connect_errno($con))
{
   echo "Failed to connect to MySQL: " . mysqli_connect_error();
}
$chefId = $_GET['chefId'];
$chefId=intval($chefId);

    $data=array(
        "recipes"=>array(
        )
    );
$result = mysqli_query($con,"SELECT * FROM recipes where chefid='$chefId'");
while($row = mysqli_fetch_array($result)){
    
   
    $moddata=array("id"=>$row[0],"name"=>$row[1],"chefId"=>$row[2],"ingridients"=>$row[3],"procedure"=>$row[4],"url"=>$row[5]);
    array_push($data["recipes"],$moddata);
        
}
$data=json_encode($data);
echo $data;
mysqli_close($con);
?>