<?php
$con=mysqli_connect("mysql17.000webhost.com","a4376707_prii","prince123","a4376707_letcook");
if (mysqli_connect_errno($con))
{
   echo "Failed to connect to MySQL: " . mysqli_connect_error();
}


    $data=array(
        "chefs"=>array(
        ),
        "recipes"=>array(
        )
    );
$result = mysqli_query($con,"SELECT * FROM chefs");
while($row = mysqli_fetch_array($result)){
   
    
    $moddata=array("id"=>$row[0],"name"=>$row[1],"url"=>$row[2]);
    array_push($data["chefs"],$moddata);
        
}
$result = mysqli_query($con,"SELECT * FROM recipes");
while($row = mysqli_fetch_array($result)){
   
    
    $moddata=array("id"=>$row[0],"name"=>$row[1],"chefId"=>$row[2],"ingridients"=>$row[3],"procedure"=>$row[4],"url"=>$row[5]);
    array_push($data["recipes"],$moddata);
        
}
$data=json_encode($data);
echo $data;
mysqli_close($con);
?>