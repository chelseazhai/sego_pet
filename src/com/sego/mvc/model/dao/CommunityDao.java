package com.sego.mvc.model.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;

import com.richitec.dao.BaseDao;
import com.richitec.util.DistanceUtil;
import com.richitec.util.RandomString;
import com.sego.mvc.controller.PetInfoController;
import com.sego.mvc.model.bean.LeaveMsg;
import com.sego.mvc.model.bean.LeaveMsgs;
import com.sego.mvc.model.bean.PetInfo;
import com.sego.mvc.model.bean.PetInfos;
import com.sego.table.LeaveMsgColumn;
import com.sego.table.LocationColum;

@Transactional
public class CommunityDao extends BaseDao {
	private static Log log = LogFactory.getLog(CommunityDao.class);
	private static final double MAX_DISTANCE = 3000; // km
	public int getTotalPetsWithPhoto() {
		String sql = "SELECT count(p.petid) FROM f_pets AS p WHERE p.ownerid IN (SELECT DISTINCT(ownerid) FROM photo GROUP BY ownerid)";
		return jdbc.queryForInt(sql);
	}
	
	public PetInfos getRecommendedPets(String userName) {
		int total = getTotalPetsWithPhoto();
		int start = 0;
		final int pageSize = 8;
		if (total > pageSize) {
			int diff = total - pageSize + 1;
			Random random = new Random();
			start = random.nextInt(diff);
		}
		log.info("getRecommendedPets start: " + start);
		
		String sql = "SELECT p.* FROM f_pets AS p WHERE p.ownerid IN (SELECT DISTINCT(ownerid) FROM photo GROUP BY ownerid) LIMIT ?,?";
		List<Map<String, Object>> list = jdbc.queryForList(sql, start, pageSize);
		PetInfos petInfos = new PetInfos();
		List<PetInfo> petInfoList = new ArrayList<PetInfo>();
		petInfos.setList(petInfoList);
		if (list != null) {
			for (Map<String, Object> map : list) {
				PetInfo petInfo = PetInfoDao.convertMapToPetInfo(map);
				petInfoList.add(petInfo);
			}
		}
		return petInfos;
	}

	public PetInfos getNearbyPets(double lng, double lat) {
		String sql = "SELECT * FROM f_location WHERE ABS(longitutde - ?) < 1 AND ABS(latitude - ?) < 1";
		List<Map<String, Object>> list = jdbc.queryForList(sql, lng, lat);
		StringBuffer idBuffer = new StringBuffer();
		if (list != null) {
			for (Map<String, Object> map : list) {
				String longitude = String.valueOf(map
						.get(LocationColum.longitude.name()));
				String latitude = String.valueOf(map.get(LocationColum.latitude
						.name()));
				int id = (Integer) map.get(LocationColum.id.name());
				double lng2 = Double.parseDouble(longitude);
				double lat2 = Double.parseDouble(latitude);
				if (DistanceUtil.getDistance(lng, lat, lng2, lat2) < MAX_DISTANCE) {
					idBuffer.append(id).append(',');
				}
			}
			if (idBuffer.toString().endsWith(",")) {
				idBuffer.deleteCharAt(idBuffer.length() - 1);
			}
		}
		PetInfos petInfos = new PetInfos();
		List<PetInfo> petInfoList = new ArrayList<PetInfo>();
		petInfos.setList(petInfoList);
		if (idBuffer.length() > 0) {
			sql = "SELECT * FROM f_pets WHERE petid IN (" + idBuffer.toString()
					+ ")";
			List<Map<String, Object>> petList = jdbc.queryForList(sql);
			if (petList != null) {
				for (Map<String, Object> map : petList) {
					PetInfo petInfo = PetInfoDao.convertMapToPetInfo(map);
					petInfoList.add(petInfo);
				}
			}
		}

		return petInfos;
	}

	public PetInfos getConcernedPets(String userName) {
		String sql = "SELECT p.* FROM f_pets AS p JOIN f_guanzhu AS g ON p.petid = g.petid WHERE g.loginid = ?";
		List<Map<String, Object>> petList = jdbc.queryForList(sql, userName);
		PetInfos petInfos = PetInfoDao.convertListToPetInfos(petList);
		return petInfos;
	}

	public int concernPet(String userName, String petId) {
		String sql = "SELECT count(id) FROM f_guanzhu WHERE loginid = ? AND petid = ?";
		int count = jdbc.queryForInt(sql, userName, petId);
		if (count > 0) {
			return -2; // already concerned
		} else {
			String sql1 = "INSERT INTO f_guanzhu (loginid, petid) VALUES(?,?)";
			return jdbc.update(sql1, userName, petId);
		}
	}

	public int unconcernPet(String userName, String petId) {
		String sql = "DELETE FROM f_guanzhu WHERE loginid = ? AND petid = ?";
		return jdbc.update(sql, userName, petId);
	}

	public int leaveMsg(String userName, String petId, String content,
			String parentId) {
		String sql = "INSERT INTO f_liuyan (author, content, petid, parentid) VALUES(?, ?, ?, ?)";
		return jdbc.update(sql, userName, content, petId, parentId);
	}

	public int replyMsg(String userName, String content, String parentId) {
		String petId = getPetIdByMsgId(parentId);
		return leaveMsg(userName, petId, content, parentId);
	}

	public boolean isMsgExist(String msgId) {
		String sql = "SELECT count(id) FROM f_liuyan WHERE id = ?";
		int count = jdbc.queryForInt(sql, msgId);
		return count > 0;
	}

	public String getPetIdByMsgId(String msgId) {
		String sql = "SELECT petid FROM f_liuyan WHERE id = ?";
		return jdbc.queryForObject(sql, String.class, msgId);
	}

	public int delMsg(String msgId) {
		String sql = "DELETE FROM f_liuyan WHERE id = ?";
		return jdbc.update(sql, msgId);
	}

	public LeaveMsgs getLeaveMsgsByUser(String userName, String petId) {
		String sql = "SELECT l.id as id, l.author as author, l.content as content, l.petid as petid, l.parentid as parentid, UNIX_TIMESTAMP(l.date) AS _date, "
				+ " p.nickname as leaver_nickname, p.sex as leaver_sex, p.avatar as leaver_avatar "
				+ " FROM f_liuyan AS l, f_pets AS p WHERE l.author = p.ownerid AND (l.petid = ? OR l.author = ?) AND l.parentid = 0 ";
		List<Map<String, Object>> list = jdbc.queryForList(sql, petId, userName);
		LeaveMsgs leaveMsgs = new LeaveMsgs();
		List<LeaveMsg> msgList = new ArrayList<LeaveMsg>();
		leaveMsgs.setList(msgList);
		if (list != null) {
			for (Map<String, Object> map : list) {
				if (map.get(LeaveMsgColumn.id.name()) != null) {
					msgList.add(convertMapToLeaveMsg(map));
				}
			}
		}
		return leaveMsgs;
	}
	
	
//	public LeaveMsgs getLeaveMsgs(String petId) {
//		String sql = "SELECT l.id as id, l.author as author, l.content as content, l.petid as petid, l.parentid as parentid, UNIX_TIMESTAMP(l.date) AS _date, "
//				+ " p.nickname as leaver_nickname, p.sex as leaver_sex, p.avatar as leaver_avatar "
//				+ " FROM f_liuyan AS l, f_pets AS p WHERE l.petid = ? AND l.author = p.ownerid";
//		List<Map<String, Object>> list = jdbc.queryForList(sql, petId);
//		LeaveMsgs leaveMsgs = new LeaveMsgs();
//		List<LeaveMsg> msgList = new ArrayList<LeaveMsg>();
//		leaveMsgs.setList(msgList);
//		if (list != null) {
//			for (Map<String, Object> map : list) {
//				msgList.add(convertMapToLeaveMsg(map));
//			}
//		}
//		return leaveMsgs;
//	}

	private LeaveMsg convertMapToLeaveMsg(Map<String, Object> map) {
		LeaveMsg msg = new LeaveMsg();
		
		msg.setMsgid((Integer) (map.get(LeaveMsgColumn.id.name())));
		msg.setAuthor((String) (map.get(LeaveMsgColumn.author.name())));
		msg.setContent((String) (map.get(LeaveMsgColumn.content.name())));
		msg.setPetid((Integer)(map.get(LeaveMsgColumn.petid.name())));
		msg.setParentid((Integer)(map.get(LeaveMsgColumn.parentid.name())));
		msg.setLeave_timestamp((Long)(map.get("_date")));
		msg.setLeaver_nickname((String)(map.get("leaver_nickname")));
		Integer sex = (Integer) (map.get("leaver_sex"));
		msg.setLeaver_sex(sex == null ? 0 : sex);
		msg.setLeaver_avatar((String) (map.get("leaver_avatar")));
		return msg;
	}

	public LeaveMsgs getRelatedMsgs(String msgId) {
		String sql = "SELECT l.id as id, l.author as author, l.content as content, l.petid as petid, l.parentid as parentid, UNIX_TIMESTAMP(l.date) AS _date, "
				+ "  p.nickname as leaver_nickname, p.sex as leaver_sex, p.avatar as leaver_avatar "
				+ "   FROM ((SELECT * FROM f_liuyan AS liu WHERE liu.parentid = 0) "
				+ "   UNION (SELECT c.* FROM f_liuyan AS parent LEFT JOIN f_liuyan AS c ON c.parentid = parent.id WHERE parent.id = ?)) AS l LEFT JOIN "
				+ "   f_pets AS p ON l.author = p.ownerid ";
		
		List<Map<String, Object>> list = jdbc.queryForList(sql, msgId);
		LeaveMsgs leaveMsgs = new LeaveMsgs();
		List<LeaveMsg> msgList = new ArrayList<LeaveMsg>();
		leaveMsgs.setList(msgList);
		if (list != null) {
			for (Map<String, Object> map : list) {
				if (map.get(LeaveMsgColumn.id.name()) != null) {
					msgList.add(convertMapToLeaveMsg(map));
				}
			}
		}
		return leaveMsgs;
	}
	
	public LeaveMsg getLeaveMsgDetail(String msgId) {
		String sql = "SELECT l.id as id, l.author as author, l.content as content, l.petid as petid, l.parentid as parentid, UNIX_TIMESTAMP(l.date) AS _date, "
				+ " p.nickname as leaver_nickname, p.sex as leaver_sex, p.avatar as leaver_avatar "
				+ " FROM f_liuyan AS l, f_pets AS p WHERE l.id = ? AND l.author = p.ownerid";
		Map<String, Object> map = jdbc.queryForMap(sql, msgId);
		return convertMapToLeaveMsg(map);
	}
	
	public PetInfos getPetBlackList(String userName) {
		String sql = "SELECT * FROM f_pets WHERE petid IN " +
				"( SELECT blackpetid FROM f_blacklist WHERE loginid = ?) ";
		List<Map<String, Object>> list = jdbc.queryForList(sql, userName);
		return PetInfoDao.convertListToPetInfos(list);
	}
	
	public boolean isPetInBlackList(String petId, String userName) {
		String sql = "SELECT count(id) FROM f_blacklist WHERE loginid = ? AND blackpetid = ?";
		int count = jdbc.queryForInt(sql, userName, petId);
		return count > 0;
	}
	
	public int addPetToBlackList(String petId, String userName) {
		String sql = "INSERT INTO f_blacklist (loginid, blackpetid) VALUES(?,?)";
		return jdbc.update(sql, userName, petId);
	}
	
	public int delPetFromBlackList(String petId, String userName) {
		String sql = "DELETE FROM f_blacklist WHERE loginid = ? AND blackpetid = ?";
		return jdbc.update(sql, userName, petId);
	}
}
