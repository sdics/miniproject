package site.metacoding.miniproject.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.metacoding.miniproject.domain.company.CompanyDao;
import site.metacoding.miniproject.domain.notice.Notice;
import site.metacoding.miniproject.domain.notice.NoticeDao;
import site.metacoding.miniproject.domain.person.Person;
import site.metacoding.miniproject.domain.person.PersonDao;
import site.metacoding.miniproject.domain.person_skill.PersonSkill;
import site.metacoding.miniproject.domain.person_skill.PersonSkillDao;
import site.metacoding.miniproject.domain.recommend.Recommend;
import site.metacoding.miniproject.domain.recommend.RecommendDao;
import site.metacoding.miniproject.domain.resume.Resume;
import site.metacoding.miniproject.domain.resume.ResumeDao;
import site.metacoding.miniproject.domain.submit_resume.SubmitResume;
import site.metacoding.miniproject.domain.submit_resume.SubmitResumeDao;
import site.metacoding.miniproject.domain.user.User;
import site.metacoding.miniproject.domain.user.UserDao;
import site.metacoding.miniproject.web.dto.request.PersonJoinDto;
import site.metacoding.miniproject.web.dto.request.ResumeWriteDto;
import site.metacoding.miniproject.web.dto.response.InterestPersonDto;
import site.metacoding.miniproject.web.dto.response.NoticeApplyDto;
import site.metacoding.miniproject.web.dto.response.PersonInfoDto;
import site.metacoding.miniproject.web.dto.response.PersonRecommendListDto;
import site.metacoding.miniproject.web.dto.response.ResumeDetailFormDto;
import site.metacoding.miniproject.web.dto.response.ResumeFormDto;
import site.metacoding.miniproject.web.dto.response.RecommendDetailDto;
import site.metacoding.miniproject.web.dto.response.AppliersDto;
import site.metacoding.miniproject.web.dto.response.CompanyRecommendDto;
import site.metacoding.miniproject.web.dto.response.InterestPersonDto;
import site.metacoding.miniproject.web.dto.response.NoticeRespDto;

@RequiredArgsConstructor
@Service
public class PersonService {
	
	private final PersonDao personDao;
	private final UserDao userDao;
	private final PersonSkillDao personSkillDao;
	private final ResumeDao resumeDao;
	private final CompanyDao companyDao;
	private final RecommendDao recommendDao; 
	private final SubmitResumeDao submitResumeDao;
	private final NoticeDao noticeDao;

	@Transactional(rollbackFor = {RuntimeException.class})
	public void ????????????(PersonJoinDto personJoinDto) {
		userDao.insert(personJoinDto.toUser());
		User userPS = userDao.findByUsername(personJoinDto.getUsername());
		personDao.insert(personJoinDto.toPerson(userPS.getUserId()));
		Integer personId = personDao.findToId(userPS.getUserId());
		List<String> personSkillList = personJoinDto.getPersonSkillList();
		for(int i=0;i<personSkillList.size();i++) {
			personSkillDao.insert(personId, personSkillList.get(i));
		}
	}


	public ResumeFormDto ???????????????????????????(Integer personId) {
		Person person = personDao.findById(personId);
		ResumeFormDto resumeFormDto = new ResumeFormDto(personId, person.getUserId(), person.getPersonName(), person.getPersonEmail(),
				person.getDegree(), person.getAddress(), person.getCareer(), personSkillDao.findByPersonId(personId));
		return resumeFormDto;
	}
	
	
	public List<Integer> ??????????????????????????????(List<String> skillList){

		List<Integer> interesPersonIdList = new ArrayList<Integer>();

		List<Person> personList = personDao.findAll();
		
		for(int i=0; i<personList.size(); i++) {
			int count = 0;
			int personId = personList.get(i).getPersonId();
			for(int j=0; j<skillList.size(); j++) {
				if(personSkillDao.findBySkillAndPersonId(skillList.get(j) , personId) ==null) {
					continue;
				}
				count++;
			}
			if(count>=skillList.size()) {
				interesPersonIdList.add(personId);
			}
		}
		
		return interesPersonIdList;
	}

	public List<Integer> ??????????????????????????????(String degree) {
		List<Integer> personIdList = personDao.findByDegree(degree);
		return personIdList;
	}
	
	public List<Integer> ??????????????????????????????(Integer career){
		List<Integer> personIdList = personDao.findByCareer(career);
		return personIdList; 
	}


	public List<InterestPersonDto> ????????????????????????(List<Integer> personIdList){
		List<InterestPersonDto> interestPersonDtoList = new ArrayList<InterestPersonDto>();
		int count = 0;
		
		for (int i = 0; i < personIdList.size(); i++) {
			count++;
			Person person = personDao.findById(personIdList.get(i));
			RecommendDetailDto CompanyDetailRecomDto = recommendDao.findAboutsubject(null, person.getUserId());
			InterestPersonDto  interestPersonDto = new InterestPersonDto(person.getPersonId(), CompanyDetailRecomDto.getRecommendCount(), person.getPersonName(), person.getCareer(), person.getDegree(), person.getAddress(), personSkillDao.findByPersonId(personIdList.get(i)));
			
			interestPersonDtoList.add(interestPersonDto);
			if(count>=20) {
				break;
			}
		}
		
		return interestPersonDtoList;
	}


	public void ???????????????(ResumeWriteDto resumeWriteDto, Integer personId) {
		Resume resume = resumeWriteDto.toEntity(personId);
		resumeDao.insert(resume);
	}

	public Integer ????????????????????????(Integer userId) {
		return personDao.findToId(userId);
	}
	
	public List<PersonInfoDto> ??????????????????(Integer personId){
		return personDao.personInfo(personId);
	}

	public List<PersonInfoDto> ??????????????????(Integer personId){
		return personSkillDao.personSkillInfo(personId);
	}
	
	public List<PersonRecommendListDto>??????????????????????????????(){
		List<PersonRecommendListDto> personRecommendListDto = personDao.findToPersonRecommned();
		for (int i = 0; i < personRecommendListDto.size(); i++) {
			Integer personId = personRecommendListDto.get(i).getPersonId();
			personRecommendListDto.get(i).setSkill(personSkillDao.findByPersonId(personId));
		}
		return  personRecommendListDto;
	}
	
	public List<Resume> ???????????????????????????(Integer userId){
		List<Resume> resumeList = resumeDao.findByPersonId(personDao.findToId(userId));
		if(resumeList.size()==0) {
			return null;
		}
		System.out.println(resumeList.get(0));
		return resumeList;
	}
	
	public void ?????????????????????(Integer resumeId) {
		resumeDao.deleteById(resumeId);
	}
	
	public List<PersonRecommendListDto> ???????????????????????????(){
		List<PersonRecommendListDto> PersonRecommendDtoList = personDao.findToPersonRecommned();
		for (int i = 0; i < PersonRecommendDtoList.size(); i++) {
			PersonRecommendDtoList.get(i).setSkill(personSkillDao.findByPersonId(PersonRecommendDtoList.get(i).getPersonId()));
		}
		return PersonRecommendDtoList;
	}


	public RecommendDetailDto ???????????????????????????(Integer userId, Integer subjectId) {
		return recommendDao.findAboutsubject(userId, subjectId);
	}
	
	public void ?????????????????????(Integer userId, Integer subjectId) {
		Recommend recommend = new Recommend(null, userId, subjectId, null);
		recommendDao.insert(recommend);
	}
	public void ?????????????????????(Integer recommendId) {
		recommendDao.delete(recommendId);
	}
	
	public List<AppliersDto> ????????????????????????(Integer noticeId) {
		List<SubmitResume> submitResumedList = submitResumeDao.findByNoticeId(noticeId);
		List<AppliersDto> appliersDtoList = new ArrayList<>();
		for (int i = 0; i < submitResumedList.size(); i++) {
			Integer personId = resumeDao.findById(submitResumedList.get(i).getResumeId()).getPersonId();
			Person person = personDao.findById(personId);
			personDao.findById(personId);
			List<PersonSkill> personSkillList =  personSkillDao.findByPersonId(personId);
			appliersDtoList.add(new AppliersDto(submitResumedList.get(i).getResumeId(), personId, person.getPersonName(), person.getCareer(), personSkillList, submitResumedList.get(i).getCreatedAt()));
		}
		return appliersDtoList;
	}
	
	public Notice ????????????????????????(int noticeId) {
		return noticeDao.findById(noticeId);
	}


	public void ??????????????????(Integer noticeId) {
		noticeDao.closeNotice(noticeId, true);
		
	}


	public List<NoticeApplyDto> ??????????????????(Integer userId) {
		return noticeDao.findNoticeApply(userId);
	}


	
}
