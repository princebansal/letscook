<?php
$con=mysqli_connect("mysql17.000webhost.com","a4376707_prii","prince123","a4376707_letcook");
if (mysqli_connect_errno($con))
{
   echo "Failed to connect to MySQL: " . mysqli_connect_error();
}
$fav = $_GET['fav'];
$fav_array;


if($fav!=NULL)
{
    $fav_array=explode(",",$fav);
    
}

    $data=array(
        "chefs"=>array(
        )
    );
$result = mysqli_query($con,"SELECT * FROM chefs");
while($row = mysqli_fetch_array($result)){
    if($fav==NULL)
    {
        $favtext="none";
    
    }
    else{
        if(in_array(strval($row[0]),$fav_array)){
            $favtext="true";
        }
        else
            $favtext=false;
    }
    
    $moddata=array("id"=>$row[0],"name"=>$row[1],"url"=>$row[2],"favourite"=>$favtext);
    array_push($data["chefs"],$moddata);
        
}
$data=json_encode($data);
echo $data;
mysqli_close($con);
?>