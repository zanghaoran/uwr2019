package core.generator;
import core.common.*;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DataSourceConfig.class)
@PowerMockIgnore("javax.management.*")
public class TestTemplateProcessor implements DataSourceType{
	//待测试类(SUT)的一个实例。
	private TemplateProcessor tp;
	//依赖类(DOC)的一个实例。
	private DataSourceConfig dsc;

	@Test
	public void testStaticVarExtract() throws Exception {

		//设置待测试类的状态（测试目标方法）
		tp.staticVarExtract("resource/newtemplatezzz.doc");
		//以下进行检查点设置
		DataSource ds = dsc.getConstDataSource();

		List<DataHolder> dhs = ds.getVars();
		DataHolder dh1 = ds.getDataHolder("sex");
		assertNotNull("变量sex解析为空", dh1);
		assertEquals("变量sex值获取错误","Female",dh1.getValue());

		DataHolder dh2 = ds.getDataHolder("readme");
		assertNotNull("变量readme解析为空", dh2);
		assertEquals("变量readme值获取错误","5",dh2.getValue());

		DataHolder dh3 = ds.getDataHolder("testexpr");
		assertNotNull("变量testexpr", dh3);
		assertEquals("变量testexpr的表达式解析错误","${num}+${readme}",dh3.getExpr());
		dh3.fillValue();
		assertEquals("变量testexpr","5.0",dh3.getValue());

		//检测SUT的实际行为模式是否符合预期
		PowerMock.verifyAll();
	}

	@Before
	public void setUp() throws Exception {

		//以下采用Mock对象的方式，做测试前的准备。
		//与以上方法比较，好处是降低SUT（TemplateProcessor类）与DOC（DataSourceConfig类）之间的耦合性，解耦它们。
		//从而使得定位缺陷变得容易。
		//参照流程：
		//1. 使用EasyMock建立一个DataSourceConfig类的一个Mock对象实例；
		//2. 录制该实例的STUB模式和行为模式（针对的是非静态方法）；
		//3. 使用PowerMock建立DataSourceConfig类的静态Mock；
		//4. 录制该静态Mock的行为模式（针对的是静态方法）；
        //------------------------------------------------
        //以上流程请在这里实现：
        //

		DataSourceConfig dsc = Easymock.createMock(DataSourceConfig.class);

		ArrayList<DataHolder> holder = new ArrayList<>();

		ConstDataSource cds = EasyMock.createMock(ConstDataSource.class);

		DataHolder dh1 = EasyMock.createMock(DataHolder.class);
		EasyMock.expect(dh1.getValue()).andReturn("Female");

		DataHolder dh2 = EasyMock.createMock(DataHolder.class);
		EasyMock.expect(dh2.getValue()).andReturn("5");

		DataHolder dh3 = EasyMock.createMock(DataHolder.class);
		EasyMock.expect(dh3.getValue()).andReturn("5.0");
		EasyMock.expect(dh3.getExpr()).andReturn("${num}+${readme}");
		EasyMock.expect(dh3.fillValue()).andReturn(null);

		holder.add(dh1);
		holder.add(dh2);
		holder.add(dh3);
		EasyMock.expect(cds.getDataHolder("sex")).andReturn(dh1);
		EasyMock.expect(cds.getDataHolder("readme")).andReturn(dh2);
		EasyMock.expect(cds.getDataHolder("testexpr")).andReturn(dh3);

		EasyMock.replay(cds,dh1,dh2,dh3);

		PowerMock.mockStatic(DataSourceConfig.class);
		EasyMock.expect(DataSourceConfig.newInstance()).andStubReturn(dsc);

        // 
        //
        //------------------------------------------------
		//5. 重放所有的行为。
		PowerMock.replayAll(dsc);
		//初始化一个待测试类（SUT）的实例
		tp = new TemplateProcessor();
	}
}

