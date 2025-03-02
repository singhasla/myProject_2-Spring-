package controller;

import java.io.File;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import model.BoardBean;
import model.MemberBean;
import model.ShoppingDAO;
import model.SuBean;
import model.SuCartBean;

public class ShoppingController {

	ShoppingDAO shoppingDao;

	public void setShoppingDao(ShoppingDAO shoppingDao) {
		this.shoppingDao = shoppingDao;
	}

	@RequestMapping("/index.do")
	public ModelAndView index(HttpSession session){
		
		ModelAndView mav = new ModelAndView();

		MemberBean mbean = (MemberBean)session.getAttribute("mbean");
		
		if(mbean==null){
			mav.addObject("mbean",null);
			mav.setViewName("ShoppingMain");
		} else {
			mav.addObject("mbean",mbean);
			mav.setViewName("ShoppingMain");
		}
		
		return mav;
	}
	
	@RequestMapping("/joinform.do")
	public ModelAndView joinForm(){
		
		ModelAndView mav = new ModelAndView();
		
		mav.addObject("center","JoinForm.jsp");
		mav.addObject("left","SujakLeft.jsp");
		mav.setViewName("ShoppingMain");
		
		return mav;
	}
	
	@RequestMapping("/joinproc.do")
	public ModelAndView joinProc(MemberBean mbean, HttpSession session){
		
		ModelAndView mav = new ModelAndView();
		
		int result = shoppingDao.getLogin(mbean);
		
		if(result==1){//아이디 중복일 경우
			mav.addObject("result","1");
			mav.addObject("center","JoinForm.jsp");
			mav.addObject("left","SujakLeft.jsp");
			mav.setViewName("ShoppingMain");
			
		} else {//아이디 중복하지 않을 경우
			//비밀번호 두개 일치할 경우
			if(mbean.getMempasswd1().equals(mbean.getMempasswd2())){
				shoppingDao.insertMember(mbean);
				session.setAttribute("mbean", mbean);
				session.setMaxInactiveInterval(60*30);//세션유지 30분
				
				return new ModelAndView(new RedirectView("index.do"));
			
			} else {////비밀번호 두개 일치하지 않을 경우
				mav.addObject("result","2");
				mav.addObject("center","JoinForm.jsp");
				mav.addObject("left","SujakLeft.jsp");
				mav.setViewName("ShoppingMain");
			}
		}
		
		return mav;
	}
	
	@RequestMapping("/login.do")
	public ModelAndView login(){
		
		ModelAndView mav = new ModelAndView();
		
		mav.addObject("center","LoginForm.jsp");
		mav.addObject("left","SujakLeft.jsp");
		mav.setViewName("ShoppingMain");
		
		return mav;
	}
	
	@RequestMapping("/loginproc.do")
	public ModelAndView loginProc(MemberBean mbean, HttpSession session){
		
		ModelAndView mav = new ModelAndView();
		
		int result = shoppingDao.getLoginProc(mbean);
		
		if(result == 1){//회원이 존재할 경우
			session.setAttribute("mbean", mbean);
			session.setMaxInactiveInterval(60*30);//세션유지 30분
			
			return new ModelAndView(new RedirectView("index.do"));
		
		} else {//로그인 할 수 없다면 LoginForm.jsp페이지로 이동해서 1전달
			mav.addObject("login","1");
			mav.addObject("center","LoginForm.jsp");
			mav.addObject("left","SujakLeft.jsp");
			mav.setViewName("ShoppingMain");
			return mav;
		}
	}
	
	@RequestMapping("/logout.do")
	public ModelAndView logout(HttpSession session){
		
		MemberBean mbean = (MemberBean)session.getAttribute("mbean");
		
		//session.invalidate();
		session.setAttribute("mbean", null);
		
		return new ModelAndView(new RedirectView("index.do"));
	}
	
	@RequestMapping("/sujak.do")
	public ModelAndView sujak(String num){
		
		ModelAndView mav = new ModelAndView();

		if(num==null){
			List<SuBean> list = shoppingDao.getAllSutool();
			mav.addObject("list",list);
		} else {
			List<SuBean> list = shoppingDao.getSelectSutool(num);
			mav.addObject("list",list);
		}
		
		mav.addObject("center","SujakCenter.jsp");
		mav.addObject("left","SujakLeft.jsp");
		mav.setViewName("ShoppingMain");
		
		return mav;
	}
	
	@RequestMapping("/suinfo.do")
	public ModelAndView suinfo(int suno){
		
		SuBean sbean = shoppingDao.getOneSutool(suno);
		
		ModelAndView mav = new ModelAndView();
		
		mav.addObject("sbean", sbean);
		mav.addObject("center","SujakInfo.jsp");
		mav.addObject("left","SujakLeft.jsp");
		mav.setViewName("ShoppingMain");
		
		return mav;
	}
	
	@RequestMapping("/sutoolcart.do")
	public ModelAndView sutoolCart(SuCartBean cartbean, HttpSession session){
		
		Cart cart = (Cart)session.getAttribute("cart");
		
		if(cart==null){
			cart = new Cart();
			session.setAttribute("cart", cart);
		}
		
		cart.push(cartbean);
		
		ModelAndView mav = new ModelAndView();
		
		mav.addObject("msg", cartbean.getSuname() + "의 상품 " 
							+ cartbean.getSuqty() + "개를 카트에 추가했습니다.");
		mav.addObject("center","SuCartResult.jsp");
		mav.addObject("left","SujakLeft.jsp");
		mav.setViewName("ShoppingMain");
		
		return mav;
	}
	
	@RequestMapping("/sucartdel.do")
	public ModelAndView sucartDel(int suno, HttpSession session){
		
		Cart cart = (Cart)session.getAttribute("cart");
		
		cart.deleteCart(suno);
		ModelAndView mav = new ModelAndView();
		
		mav.addObject("cart",cart);
		mav.addObject("center","SuCartResult.jsp");
		mav.addObject("left","SujakLeft.jsp");
		mav.setViewName("ShoppingMain");
		
		return mav;
	}
	
	//상품 바로구매
	@RequestMapping("/sutoolbuy.do")
	public ModelAndView sutoolBuy(SuCartBean subean, HttpSession session){
		
		ModelAndView mav = new ModelAndView();

		MemberBean mbean = (MemberBean)session.getAttribute("mbean");
		
		if(mbean==null){
			mav.addObject("center","LoginForm.jsp");
			mav.addObject("left","SujakLeft.jsp");
			mav.setViewName("ShoppingMain");
		} else {
			mav.addObject("subean",subean);
			mav.addObject("center","SutoolBuy.jsp");
			mav.addObject("left","SujakLeft.jsp");
			mav.setViewName("ShoppingMain");
		}
		
		return mav;
	}
	
	//카트에 담긴 상품 구매
	@RequestMapping("/sucartbuy.do")
	public ModelAndView sucartBuy(SuCartBean subean, HttpSession session){
		
		ModelAndView mav = new ModelAndView();

		MemberBean mbean = (MemberBean)session.getAttribute("mbean");
		
		if(mbean==null){
			mav.addObject("center","LoginForm.jsp");
			mav.addObject("left","SujakLeft.jsp");
			mav.setViewName("ShoppingMain");
		} else {
			
			Cart cart = (Cart)session.getAttribute("cart");
			
			mav.addObject("cart",cart);
			mav.addObject("subean",subean);
			mav.addObject("center","SuCartBuy.jsp");
			mav.addObject("left","SujakLeft.jsp");
			mav.setViewName("ShoppingMain");
		}
		
		return mav;
	}
		
	//카트 물품 비우기
	@RequestMapping("/cartalldel.do")
	public ModelAndView cartallDel(HttpSession session){
		
		Cart cart = (Cart)session.getAttribute("cart");
		cart.clearCart();
		
		return new ModelAndView(new RedirectView("index.do"));
	}
	
	//소개
	@RequestMapping("/stanlyinfo.do")
	public ModelAndView stanlyInfo(String name){

		String[] imgarr = {"stanlycenterinfo","stanlycenterhistory1","stanlycenterglobal","stanlycentercompany"};
		
		if(name==null){
			name="0";
		}
			
		ModelAndView mav = new ModelAndView();
		mav.addObject("imgname",imgarr[Integer.parseInt(name)]);
		mav.addObject("center","StanlyInfoMain.jsp");
		mav.addObject("left","StanlyInfoLeft.jsp");
		mav.setViewName("ShoppingMain");
		
		return mav;
	}
	
	//사용법
	@RequestMapping("tooluse.do")
	public ModelAndView toolUse(String name){
		
		String [] imgarr={"tool1","tool2","tool3","tool4","tool5", "tool6","tool7","tool8-1","tool9","tool10"};

		if(name==null){
			name ="0";
		}

		ModelAndView mav = new ModelAndView();
		mav.addObject("imgname", imgarr[Integer.parseInt(name)]);
		mav.addObject("center", "ToolUseCenter.jsp");
		mav.addObject("left", "ToolUseLeft.jsp");		
		mav.setViewName("ShoppingMain");
		return mav;	

	}
	
	@RequestMapping("download.do")
	public ModelAndView downLoad(){

		ModelAndView mav = new ModelAndView();
		mav.addObject("center", "DownCenter.jsp");
		mav.addObject("left", "DownLeft.jsp");		
		mav.setViewName("ShoppingMain");
		return mav;	

	}
	
	//다운로드 요청
	@RequestMapping("downfile.do")
	public ModelAndView downFile(int no){
		
		String [] filename={"m0.pdf", "m1.pdf", "m2.pdf"};

		String path="D:/git/myProject_2_Spring/ShoppingMall/WebContent/downfile/";

		File downloadfile = new File(path + filename[no]);
		
		return new ModelAndView("downloadView","downloadFile", downloadfile);

	}
	
	//전체 게시글
	@RequestMapping("/board.do")
	public ModelAndView boardList(String pageNum){
		
		ModelAndView mav = new ModelAndView();
		int pageSize=10;
		
		int count =0;//전체 글 갯수
		int number =0;//현재 페이지 넘버
		
		if(pageNum == null){
			pageNum="1";
		}

		int currentPage  = Integer.parseInt(pageNum);

		//게시글 총 갯수
		count = shoppingDao.getCount();
	
		int startRow = (currentPage -1)*pageSize+1;
		int endRow = currentPage*pageSize;

		List<BoardBean> vbean=null;
		if(count > 0 ){
			//10개 기준으로 테이블에서 읽어오기
			vbean = shoppingDao.getAllContent(startRow-1 , endRow);		
			//테이블에 표시할 번호
			number = count -(currentPage -1) * pageSize;
			
		}
		//BoardList.jsp
		mav.addObject("vbean", vbean);
		mav.addObject("number", number);
		mav.addObject("pageSize", pageSize);
		mav.addObject("count", count);
		mav.addObject("currentPage", currentPage);
		mav.addObject("center", "BoardList.jsp");
		mav.addObject("left", "BoardLeft.jsp");
		mav.setViewName("ShoppingMain");
		return mav;	
		
	}
	
	@RequestMapping("/boardwrite.do")
	public ModelAndView boardWrite(){
		ModelAndView mav = new ModelAndView();
		
		mav.addObject("center", "BoardWrite.jsp");
		mav.addObject("left", "BoardLeft.jsp");
		mav.setViewName("ShoppingMain");
		return mav;	
	}
	
	@RequestMapping("/boardwriteproc.do")
	public ModelAndView boardWriteProc(BoardBean bean){
			
		shoppingDao.boardInsert(bean);
		
		return new ModelAndView(new RedirectView("board.do"));
	}
 	
	@RequestMapping("/boardinfo.do")
	public ModelAndView boardInfo(int num){
		
		ModelAndView mav = new ModelAndView();

		BoardBean bean = shoppingDao.getOneContent(num);
		
		mav.addObject("bean", bean);
		mav.addObject("center", "BoardInfo.jsp");
		mav.addObject("left", "BoardLeft.jsp");
		mav.setViewName("ShoppingMain");
		return mav;	
		
	}
	
	@RequestMapping("/boardrewrite.do")
	public ModelAndView boardRewrite(BoardBean bean){
		
		ModelAndView mav = new ModelAndView();
		
		mav.addObject("bean", bean);
		mav.addObject("center", "BoardRewrite.jsp");
		mav.addObject("left", "BoardLeft.jsp");
		mav.setViewName("ShoppingMain");
		return mav;	
	}
	
	@RequestMapping("/boardrewriteproc.do")
	public ModelAndView boardrewriteProc(BoardBean bean){
		
		shoppingDao.reWriteboard(bean);
		return new ModelAndView(new RedirectView("board.do"));
	}
	
	@RequestMapping("/boardupdate.do")
	public ModelAndView boardUpdate(BoardBean bean){
		
		ModelAndView mav = new ModelAndView();
		
		mav.addObject("bean", bean);
		mav.addObject("center", "BoardUpdate.jsp");
		mav.addObject("left", "BoardLeft.jsp");
		mav.setViewName("ShoppingMain");
		return mav;	
	}
	
	@RequestMapping("/boardupdateproc.do")
	public ModelAndView boardupdateProc(BoardBean bean){
		
		shoppingDao.boardUpdate(bean);
		return new ModelAndView(new RedirectView("board.do"));
	}
	
	@RequestMapping("/boarddelete.do")
	public ModelAndView boardDelete(int num){
	
		ModelAndView mav = new ModelAndView();
		
		mav.addObject("num", num);
		mav.addObject("center", "BoardDelete.jsp");
		mav.addObject("left", "BoardLeft.jsp");
		mav.setViewName("ShoppingMain");
		return mav;
	
	}

	@RequestMapping("/boarddeleteproc.do")
	public ModelAndView boarddeleteProc(BoardBean bean){
	
		shoppingDao.boardDelete(bean);
		return new ModelAndView(new RedirectView("board.do"));
	}
	
}