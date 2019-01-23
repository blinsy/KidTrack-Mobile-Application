<?php
$HasofGroup;
class AllFuctions{
	protected $Data;
	public function __construct(){
		require 'config.php';		
		$this->Data=$Config->ConnectToDatabase();
	}
	function addLocation($idata){
		$sql0=$this->Data->prepare("INSERT INTO codinates (activationCode,longitude,latitude) values (:idata0,:idata1,:idata2) ");
		$sql0->bindParam(':idata0',$idata['activationCode'],PDO::PARAM_STR);
		$sql0->bindParam(':idata1',$idata['longitude'],PDO::PARAM_STR);
		$sql0->bindParam(':idata2',$idata['latitude'],PDO::PARAM_STR);
		if($sql0->execute()){
			return ['status'=>TRUE];
		}else{			
			return ['status'=>FALSE];
		}
	}
	function addChild($idata){
		$sql0=$this->Data->prepare("INSERT INTO kids (activationCode,userId,name,extension) values (:idata0,:idata1,:idata2,:idata3) ");
		$sql0->bindParam(':idata0',$idata['activationCode'],PDO::PARAM_STR);
		$sql0->bindParam(':idata1',$idata['userId'],PDO::PARAM_STR);
		$sql0->bindParam(':idata2',$idata['name'],PDO::PARAM_STR);
		$sql0->bindParam(':idata3',$idata['extension'],PDO::PARAM_STR);
		if($sql0->execute()){
			$adid=$this->Data->lastInsertId();
			if(move_uploaded_file ($_FILES['extension']['tmp_name'],'../kidpics/'.$adid.'.'.$idata['extension'])){
				return [
						'status'=>TRUE,
						'childId'=>$adid,
						'kidPic'=>'http://'.$_SERVER['HTTP_HOST'].'/kidtrack/kidpics/'.$adid.'.'.$idata['extension']
						];
			}else{
				return ['status'=>FALSE,'message'=>$_FILES['extension']['error']];
			}
			
		}else{
			return ['status'=>FALSE,'message'=>$sql0->errorInfo()[2]];
		}		
	}
	function viewmykids($idata){
		$feedback=array();
		$sql0=$this->Data->prepare("SELECT * FROM bookings ");
		$sql0->execute();
		$cursor0=$sql0->fetchAll(PDO::FETCH_ASSOC);
		for($i =0; $i <sizeof($cursor0); $i++){
			$id=$cursor0[$i]['id'];
			$lastLocation =	$this->Distance(['slo'=>$idata['slo'],'sla'=>$idata['sla'],'dlo'=>$cursor0[$i]['longitude'],'dla'=>$cursor0[$i]['latitude']]);
		$feedback[]=
					[
						'childName'=>$cursor0[$i]['name'],
						'lastLocation'=>$lastLocation,
						'phone'=>$cursor0[$i]['mobile'],
						'latitude'=>$cursor0[$i]['latitude'],
						'longitude'=>$cursor0[$i]['longitude'],
						'childId'=>$id
					];
		}
		// $_SERVER[REQUEST_URI]
		return $feedback;
				
	}
	function viewLocations($activationCode){
		$sql0=$this->Data->prepare("SELECT * FROM codinates WHERE activationCode=:activationCode ");
		$sql0->bindParam(':activationCode',$activationCode,PDO::PARAM_STR);
		$sql0->execute();
		return $sql0->fetchAll(PDO::FETCH_ASSOC);		
	}
	function Login($idata){
		$feedback=Array();		
		$sql=$this->Data->prepare("SELECT * FROM Users WHERE email=:email ");
		$sql->bindParam(':email',$idata['email'],PDO::PARAM_STR);
		$sql->execute();
		$cursor=$sql->fetchAll(PDO::FETCH_ASSOC);
		$Number=SizeOf($cursor);
		if($Number>0){
			if(password_verify($idata['password'], $cursor[0]['password'])){
				$feedback['status']=TRUE;
				$feedback['postId']=$cursor[0]['post'];
				require 'password.php';
				$Deta=$Password->Info($cursor[0]['password'],$idata['password']);
				if($Deta['Status']=='TRUE'){
					$sql=$this->Data->prepare("UPDATE Users SET Password=:password WHERE email=:email ");
					$sql->bindParam(':password',$Deta['NewHashedPassword'],PDO::PARAM_STR);
					$sql->bindParam(':email',$idata['email'],PDO::PARAM_STR);
					$sql->execute();
				}
				
				
				}else{
					$feedback['status']=FALSE;
					$feedback['message']="Invalid Password Or Errocode 00002";//password not matching
					}
		}else{
			$feedback['status']=FALSE;
			$feedback['message']="Invalid Password Or Errocode 00001";//username not available
		}
		return $feedback;
	}
	function Signup($idata){
		$feedback=Array();
		require 'password.php';
		$Password=$Password->HashPassword($idata['password']);
		$sql0=$this->Data->prepare("INSERT INTO Users (email,password) values (:idata0,:idata1) ");
		$sql0->bindParam(':idata0',$idata['email'],PDO::PARAM_STR);
		$sql0->bindParam(':idata1',$Password,PDO::PARAM_STR);
		if($sql0->execute()){
			$userid=$this->Data->lastInsertId();
				$feedback['status']=TRUE;
		
		}else{
		$feedback['status']=FALSE;
		$feedback['message']=$sql0->errorInfo()[2];
		}
		return $feedback;		
	}
	function deletekid($idata){
		$sql0=$this->Data->prepare("DELETE FROM kids WHERE postid=:postid ");
		$sql0->bindParam(':postid',$idata,PDO::PARAM_STR);
		$sql0->execute();
	}
	function SavePicture($idata){
		if(move_uploaded_file ($_FILES['icon']['tmp_name'],'../icons/'.$idata['userid'].'.'.$idata['extension'])){
			$sql=$this->Data->prepare("UPDATE Users SET Extension=:Extension WHERE PostId=:PostId ");
			$sql->bindParam(':Extension',$idata['extension'],PDO::PARAM_STR);
			$sql->bindParam(':PostId',$idata['userid'],PDO::PARAM_STR);
			$sql->execute();
			return ['status'=>TRUE];
		}else{			
			return ['status'=>FALSE,'message'=>$_FILES['icon']['error']];
		}		
	
	}
	function Distance($idata) {
			$lat1=$idata['dla'];
			$lon1=$idata['dlo'];
			$lat2=$idata['sla'];
			$lon2=$idata['slo'];
			$theta = $lon1 - $lon2;
			$dist = sin(deg2rad($lat1)) * sin(deg2rad($lat2)) +  cos(deg2rad($lat1)) * cos(deg2rad($lat2)) * cos(deg2rad($theta));
			$dist = acos($dist);
			$dist = rad2deg($dist);
			$miles = $dist * 60 * 1.1515;
			$DistanceRange=$miles * 1.609344;
			if($DistanceRange<1){
				return round(($DistanceRange * 1000),1).' M';
			}else{
				return round($DistanceRange,1).' Km';
			}
		
	}
}
$HasofGroup=new AllFuctions();
?>