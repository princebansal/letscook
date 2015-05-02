<?php
$con=mysqli_connect("mysql17.000webhost.com","a4376707_prii","prince123","a4376707_letcook");
if (mysqli_connect_errno($con))
{
   echo "Failed to connect to MySQL: " . mysqli_connect_error();
}
$type = $_GET['type'];
echo $type;
$type=intval($type);
$action=$_GET['action'];
$action=intval($action);

if($action==1)
    add();
else if($action==2)
    modify();
    
function add(){
    echo $type;
    if($GLOBALS['type']==1)
    {
       $name = $_GET['name'];
        $url = $_GET['url'];
        if(mysqli_query($GLOBALS['con'],"INSERT INTO chefs values(0,'$name','$url');"));
        echo "successful add chef";
    }
    else{
         $name = $_GET['name'];
        $url = $_GET['url'];
        $chefId = $_GET['chefId'];
        $ingridients = $_GET['ingredients'];
        $procedure = $_GET['procedure'];
        $result = mysqli_query($GLOBALS['con'],"SELECT * FROM chefs where name='$chefId';");
        $row = mysqli_fetch_assoc($result);
        if(mysqli_query($GLOBALS['con'],"INSERT INTO recipes values( 0,'$name', ".$row['id'].", '$ingridients','$procedure', '$url');"));
        echo "successful add recipe";
    }
    
}
function modify(){
    if($GLOBALS['type']==1)
    {
        $id=$_GET['id'];
        $id=intval($id);
        $name = $_GET['name'];
        $url = $_GET['url'];
        if(mysqli_query($GLOBALS['con'],"UPDATE chefs set name='$name', url='$url' where id='$id';"))
        echo "successful mod chef";
    }
    else 
    {
        $id=$_GET['id'];
        $id=intval($id);
        echo "id".$id;
        $name = $_GET['name'];
        $url = $_GET['url'];
        $chefId = $_GET['chefId'];
        $ingridients = $_GET['ingredients'];
        $procedure = $_GET['procedure'];
        $result = mysqli_query($GLOBALS['con'],"SELECT * FROM chefs where name='$chefId';");
        $row = mysqli_fetch_assoc($result);
        $r=$row[0];
        
        $rs=mysqli_query($GLOBALS['con'],"UPDATE recipes set `name`='$name', `chefid`='$r', `url`='$url', `ingridients`='$ingridients',`procedure`='$procedure' where id='$id';");
            if($rs)
        echo "successful mod recipe";
            else
        echo mysqli_error($GLOBALS['con']);
    }
}
mysqli_close($con);
?>