package com.sego.mvc.model.bean;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.richitec.bean.ResultBean;

@JsonInclude(Include.NON_NULL) 
public class PetInfo extends ResultBean {
	private long petid;
	private String nickname;
	private int sex;
	private float weight;
	private String chuanganid;
	private String ownerid;
	private String avatar;
	private int breed;
	private int age;
	private float height;
	private String district;
	private String placeoftengo;
	private String deviceid;
	private int score;
	
	private List<Gallery> galleries;
	
	public long getPetid() {
		return petid;
	}

	public void setPetid(long petId) {
		this.petid = petId;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public float getWeight() {
		return weight;
	}

	public void setWeight(float weight) {
		this.weight = weight;
	}

	public String getChuanganid() {
		return chuanganid;
	}

	public void setChuanganid(String chuanganid) {
		this.chuanganid = chuanganid;
	}

	public String getOwnerid() {
		return ownerid;
	}

	public void setOwnerid(String ownerid) {
		this.ownerid = ownerid;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public int getBreed() {
		return breed;
	}

	public void setBreed(int breed) {
		this.breed = breed;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getPlaceoftengo() {
		return placeoftengo;
	}

	public void setPlaceoftengo(String placeoftengo) {
		this.placeoftengo = placeoftengo;
	}

	public String getDeviceid() {
		return deviceid;
	}

	public void setDeviceid(String deviceid) {
		this.deviceid = deviceid;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public List<Gallery> getGalleries() {
		return galleries;
	}

	public void setGalleries(List<Gallery> galleries) {
		this.galleries = galleries;
	}

	public static void main(String[] args) {
		// String json =
		// "{\"list\": [{\"petid\" : \"123\", \"nickname\" : \"kkk\" }]}";
		// PetInfo petInfo = JSONUtil.toObject(json, PetInfo.class);
		// System.out.print("petId: " + petInfo.getPetid() + " nickname: " +
		// petInfo.getNickname());

//		List<PetInfo> list = new ArrayList<PetInfo>();
//		PetInfo petInfo = new PetInfo();
////		petInfo.setPetid("12");
//		petInfo.setNickname("sdf");
//		list.add(petInfo);
//		
//		petInfo = new PetInfo();
//		petInfo.setPetid("333");
//		petInfo.setNickname("asdfasdf");
//		list.add(petInfo);
//		PetInfos petInfos = new PetInfos();
//		petInfos.setList(list);
//		
//		System.out.println(JSONUtil.toString(petInfos));
		
		String avatarFileName = UUID.randomUUID().toString();
		System.out.print(avatarFileName.length() + " " + avatarFileName);
	}
}
