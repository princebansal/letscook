<?php
$con=mysqli_connect("mysql17.000webhost.com","a4376707_prii","prince123","a4376707_letcook");
if (mysqli_connect_errno($con))
{
   echo "Failed to connect to MySQL: " . mysqli_connect_error();
}
$fav = $_GET['search'];
$fav=trim($fav);
$favtext="";
$flag=0;
    $data=array(
        "status"=>"",
        "chefs"=>array(
        )
    );
$result = mysqli_query($con,"SELECT * FROM chefs where name='$fav';");
while($row = mysqli_fetch_array($result)){
    
    if($row){
        $flag=1;
    
        $moddata=array("id"=>$row[0],"name"=>$row[1],"url"=>$row[2],"favourite"=>$favtext);
        array_push($data["chefs"],$moddata);
        $data["status"]="ok";
    }
    else {
        $flag=0;
        break;
    }
}
if($flag==0){
    echo json_encode(array(
        "status"=>"notfound",
        "chefs"=>array(
        )
    ));
}
else{

$data=json_encode($data);
echo $data;
}
mysqli_close($con);
?>