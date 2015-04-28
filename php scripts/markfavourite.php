<?php
$con=mysqli_connect("mysql17.000webhost.com","a4376707_prii","prince123","a4376707_letcook");
if (mysqli_connect_errno($con))
{
   echo "Failed to connect to MySQL: " . mysqli_connect_error();
}
$id = $_GET['chefId'];
$user=$_GET['user'];
$action=$_GET['action'];


$fav="";
$fav_array;
$result = mysqli_query($con,"SELECT * FROM Users where Username='$user';");
while($row = mysqli_fetch_array($result)){

    if($action=="true"){
        
            $fav=$row[2];
            $fav_array=explode(",",$fav);
            if($fav=="none"){
                mysqli_query($con,"UPDATE Users set favourites='$id' where Username='$user';");
                echo json_encode(array("status"=>"Marked as favourite","data"=>$id));
            }
            else{
                if($fav=="")
                    $fav2=$fav.$id;
                else
                $fav2=$fav.",".$id;
                 mysqli_query($con,"UPDATE Users set favourites='$fav2' where Username='$user';");
                echo json_encode(array("status"=>"Marked as favourite","data"=>$fav2));
            }
    }
    else{
        
        $fav=$row[2];
            if(isset($fav)&&$fav!="none")
            $fav_array=explode(",",$fav);
            if($fav=="none"){
                echo json_encode(array("status"=>"You have no favourites","data"=>"none"));
            
            }
            else{
                $key=array_search($id,$fav_array);
                unset($fav_array[$key]);
                $favv=implode(",",$fav_array);
                mysqli_query($con,"UPDATE Users set favourites='$favv' where Username='$user';");
                echo json_encode(array("status"=>"Removed from favourites","data"=>$favv));
            }
        
        
    }
    
}
mysqli_close($con);
?>