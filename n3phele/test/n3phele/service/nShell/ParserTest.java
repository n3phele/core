/**
 * @author Nigel Cook
 *
 * (C) Copyright 2010-2012. Nigel Cook. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * 
 * Licensed under the terms described in LICENSE file that accompanied this code, (the "License"); you may not use this file
 * except in compliance with the License. 
 * 
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on 
 *  an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the 
 *  specific language governing permissions and limitations under the License.
 */
package n3phele.service.nShell;

import static org.junit.Assert.fail;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.List;

import junit.framework.Assert;
import n3phele.service.model.CommandDefinition;
import n3phele.service.model.CommandImplementationDefinition;
import n3phele.service.model.ParameterType;
import n3phele.service.model.ShellFragment;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Nigel Cook
 *
 * (C) Copyright 2010. All rights reserved.
 * 
 *
 */
public class ParserTest {

	/** Complex command description parsing based on the denoiser command
	 * @throws java.lang.Exception
	 */

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	
	@Test
	public void denoiseTest() throws FileNotFoundException, ParseException, Exception {

		NParser n = new NParser(new FileInputStream("./test/denoiseTest.n"));
		CommandDefinition cd = n.parse();
		Assert.assertEquals("denoise", cd.getName());
		Assert.assertEquals("denoise an aggregate of up to 5 files in .sff.txt format, which is the output of sffinfo", cd.getDescription());
		Assert.assertTrue(cd.isPublic());
		Assert.assertTrue(cd.isPreferred());
		Assert.assertEquals("1.6", cd.getVersion());
		Assert.assertEquals(URI.create("http://www.n3phele.com/qiimeIcon"), cd.getIcon());

		//
		//	Input Files
		//
		Assert.assertEquals(7, cd.getInputFiles().size());
		Assert.assertEquals("flowgram.sff.txt", cd.getInputFiles().get(0).getName());
		Assert.assertEquals("Input flowgram file in sff.txt format\",", cd.getInputFiles().get(0).getDescription());
		Assert.assertFalse(cd.getInputFiles().get(0).isOptional());
		//
		Assert.assertEquals("flowgram1.sff.txt", cd.getInputFiles().get(1).getName());
		Assert.assertEquals("Input flowgram file 2 in sff.txt format(optional)", cd.getInputFiles().get(1).getDescription());
		Assert.assertTrue(cd.getInputFiles().get(1).isOptional());
		//
		Assert.assertEquals("flowgram2.sff.txt", cd.getInputFiles().get(2).getName());
		Assert.assertEquals("Input flowgram file 3 in sff.txt format(optional)", cd.getInputFiles().get(2).getDescription());
		Assert.assertTrue(cd.getInputFiles().get(2).isOptional());
		//
		Assert.assertEquals("flowgram3.sff.txt", cd.getInputFiles().get(3).getName());
		Assert.assertEquals("Input flowgram file 4 in sff.txt format(optional)", cd.getInputFiles().get(3).getDescription());
		Assert.assertTrue(cd.getInputFiles().get(3).isOptional());
		//
		Assert.assertEquals("flowgram4.sff.txt", cd.getInputFiles().get(4).getName());
		Assert.assertEquals("Input flowgram file 5 in sff.txt format(optional)", cd.getInputFiles().get(4).getDescription());
		Assert.assertTrue(cd.getInputFiles().get(4).isOptional());
		//
		Assert.assertEquals("sequence.fasta", cd.getInputFiles().get(5).getName());
		Assert.assertEquals("Input sequence file", cd.getInputFiles().get(5).getDescription());
		Assert.assertFalse(cd.getInputFiles().get(5).isOptional());
		//
		Assert.assertEquals("mapping.txt", cd.getInputFiles().get(6).getName());
		Assert.assertEquals("Input mapping file, has to contain field LinkerPrimerSequence. Not required if primer sequence specified", cd.getInputFiles().get(6).getDescription());
		Assert.assertTrue(cd.getInputFiles().get(6).isOptional());
		//
		//
		//	Output Files
		//
		Assert.assertEquals(5, cd.getOutputFiles().size());
		Assert.assertEquals("denoiser.log", cd.getOutputFiles().get(0).getName());
		Assert.assertEquals("Log of the denoising process\",", cd.getOutputFiles().get(0).getDescription());
		Assert.assertFalse(cd.getOutputFiles().get(0).isOptional());
		//
		Assert.assertEquals("centroids.fasta", cd.getOutputFiles().get(1).getName());
		Assert.assertEquals("The centroids of sequences clustered with 2 and more members", cd.getOutputFiles().get(1).getDescription());
		Assert.assertFalse(cd.getOutputFiles().get(1).isOptional());
		//
		Assert.assertEquals("singletons.fasta", cd.getOutputFiles().get(2).getName());
		Assert.assertEquals("Read sequences that could not be clustered\",", cd.getOutputFiles().get(2).getDescription());
		Assert.assertFalse(cd.getOutputFiles().get(2).isOptional());
		//
		Assert.assertEquals("denoiser_mapping.txt", cd.getOutputFiles().get(3).getName());
		Assert.assertEquals("Cluster to read mapping\",", cd.getOutputFiles().get(3).getDescription());
		Assert.assertFalse(cd.getOutputFiles().get(3).isOptional());
		//
		Assert.assertEquals("denoised.fasta", cd.getOutputFiles().get(4).getName());
		Assert.assertEquals("Denoised sequences for additional processing\",", cd.getOutputFiles().get(4).getDescription());
		Assert.assertFalse(cd.getOutputFiles().get(4).isOptional());
		//
		//
		//	Parameters Files
		//
		Assert.assertEquals(4, cd.getExecutionParameters().size());
		Assert.assertEquals("nnn", cd.getExecutionParameters().get(0).getName());
		Assert.assertEquals("cluster size", cd.getExecutionParameters().get(0).getDescription());
		Assert.assertEquals("8", cd.getExecutionParameters().get(0).getDefaultValue());
		Assert.assertTrue(cd.getExecutionParameters().get(0).isOptional());
		Assert.assertEquals(ParameterType.Long, cd.getExecutionParameters().get(0).getType());
		//
		Assert.assertEquals("titanium", cd.getExecutionParameters().get(1).getName());
		Assert.assertEquals("select for titanium sequencer, unselected for flx", cd.getExecutionParameters().get(1).getDescription());
		Assert.assertEquals("true", cd.getExecutionParameters().get(1).getDefaultValue());
		Assert.assertFalse(cd.getExecutionParameters().get(1).isOptional());
		Assert.assertEquals(ParameterType.Boolean, cd.getExecutionParameters().get(1).getType());
		//
		Assert.assertEquals("primer", cd.getExecutionParameters().get(2).getName());
		Assert.assertEquals("Primer sequence. Default use mapping file LinkerPrimerSequence in mapping file", cd.getExecutionParameters().get(2).getDescription());
		Assert.assertEquals("LinkerPrimerSequence", cd.getExecutionParameters().get(2).getDefaultValue());
		Assert.assertFalse(cd.getExecutionParameters().get(2).isOptional());
		Assert.assertEquals(ParameterType.String, cd.getExecutionParameters().get(2).getType());
		//
		Assert.assertEquals("spotPrice", cd.getExecutionParameters().get(3).getName());
		Assert.assertEquals("maximum hourly spot price for a virtual machine. Specifying 0 will use an on-demand instance.", cd.getExecutionParameters().get(3).getDescription());
		Assert.assertEquals("0", cd.getExecutionParameters().get(3).getDefaultValue());
		Assert.assertFalse(cd.getExecutionParameters().get(3).isOptional());
		Assert.assertEquals(ParameterType.Double, cd.getExecutionParameters().get(3).getType());
		//
		//	Instance specifications
		//
		Assert.assertEquals(2, cd.getImplementations().size());
		//
		//	Shell parsing
		//
		Shell s = new Shell(cd.getImplementations().get(0).getBody(), cd.getImplementations().get(0).getLineNo());
		SimpleNode node = s.script();
		String result = dump(node, "", ".");
		String expected = "script:null\n"
				+ ".variableAssign:\"DenoiseCluster\"\n"
				+ "..createvm:null\n"
				+ "...option:\"name\"\n"
				+ "....literalArg:\"DenoiseCluster\"\n"
				+ "...option:\"imageId\"\n"
				+ "....literalArg:\"ami-e4bf1b8d\"\n"
				+ "...option:\"minCount\"\n"
				+ "....expression:\" \"\n"
				+ ".....conditionalExpression:null\n"
				+ "......logicalORExpression:null\n"
				+ ".......logicalANDExpression:null\n"
				+ "........equalityExpression:null\n"
				+ ".........relationalExpression:null\n"
				+ "..........additiveExpression:\"-\"\n"
				+ "...........multiplicativeExpression:null\n"
				+ "............unaryExpression:null\n"
				+ ".............identifier:\"n\"\n"
				+ "...........additiveExpression:null\n"
				+ "............multiplicativeExpression:null\n"
				+ ".............unaryExpression:null\n"
				+ "..............constant:1\n"
				+ "...option:\"spotPrice\"\n"
				+ "....expression:\" \"\n"
				+ ".....conditionalExpression:null\n"
				+ "......logicalORExpression:null\n"
				+ ".......logicalANDExpression:null\n"
				+ "........equalityExpression:null\n"
				+ ".........relationalExpression:null\n"
				+ "..........additiveExpression:null\n"
				+ "...........multiplicativeExpression:null\n"
				+ "............unaryExpression:null\n"
				+ ".............identifier:\"spotPrice\"\n"
				+ "...option:\"instanceType\"\n"
				+ "....literalArg:\"c1.xlarge\"\n"
				+ "...option:\"securityGroups\"\n"
				+ "....literalArg:\"n3phele-default\"\n"
				+ "...option:\"userData\"\n"
				+ "....literalArg:\"#!/bin/bash\n"
				+ "chmod a+rwx /mnt\n"
				+ "set -x\n"
				+ "wget -q -O - https://s3.amazonaws.com/n3phele-agent/addswap | /bin/bash -s 2000\n"
				+ "wget -q -O - https://n3phele-agent.s3.amazonaws.com/n3ph-install-tgz-basic | su - -c '/bin/bash -s ubuntu ~/agent /mnt/sandbox' ubuntu\n"
				+ "ln -s /mnt/sandbox ~ubuntu/sandbox\n"
				+ "\"\n"
				+ ".createvm:null\n"
				+ "..option:\"name\"\n"
				+ "...literalArg:\"DenoiseMaster\"\n"
				+ "..option:\"imageId\"\n"
				+ "...literalArg:\"ami-e4bf1b8d\"\n"
				+ "..option:\"launchGroup\"\n"
				+ "...literalArg:\"denoise\"\n"
				+ "..option:\"minCount\"\n"
				+ "...literalArg:\"1\"\n"
				+ "..option:\"spotPrice\"\n"
				+ "...expression:\" \"\n"
				+ "....conditionalExpression:null\n"
				+ ".....logicalORExpression:null\n"
				+ "......logicalANDExpression:null\n"
				+ ".......equalityExpression:null\n"
				+ "........relationalExpression:null\n"
				+ ".........additiveExpression:null\n"
				+ "..........multiplicativeExpression:null\n"
				+ "...........unaryExpression:null\n"
				+ "............identifier:\"spotPrice\"\n"
				+ "..option:\"instanceType\"\n"
				+ "...literalArg:\"m1.xlarge\"\n"
				+ "..option:\"securityGroups\"\n"
				+ "...literalArg:\"n3phele-default\"\n"
				+ "..option:\"userData\"\n"
				+ "...literalArg:\"#!/bin/bash\n"
				+ "chmod a+rwx /mnt\n"
				+ "set -x\n"
				+ "wget -q -O - https://s3.amazonaws.com/n3phele-agent/addswap | /bin/bash -s 2000\n"
				+ "wget -q -O - https://n3phele-agent.s3.amazonaws.com/n3ph-install-tgz-basic | su - -c '/bin/bash -s ubuntu ~/agent /mnt/sandbox' ubuntu\n"
				+ "ln -s /mnt/sandbox ~ubuntu/sandbox\n"
				+ "\"\n"
				+ ".on:null\n"
				+ "..expression:\" \"\n"
				+ "...conditionalExpression:null\n"
				+ "....logicalORExpression:null\n"
				+ ".....logicalANDExpression:null\n"
				+ "......equalityExpression:null\n"
				+ ".......relationalExpression:null\n"
				+ "........additiveExpression:null\n"
				+ ".........multiplicativeExpression:null\n"
				+ "..........unaryExpression:null\n"
				+ "...........identifier:\"DenoiseMaster\"\n"
				+ "..option:\"needsAll\"\n"
				+ "..option:\"producesNone\"\n"
				+ "..pieces:null\n"
				+ "...passThru:\"function scan-hosts() { i=0; while [ $i -le 10 ]; do i=$(( $i+1)); ssh-keyscan -H $* > /tmp/known_hosts; if [\"`wc -l </tmp/known_hosts`\" -ne \"$#\" ]; then echo not done .. retrying >&2; sleep 5;else cat /tmp/known_hosts; break; fi; done };\n"
				+ " function n-times() { i=0; n=$1; shift; while [ $i -lt $n ]; do $*; i=$(( $i+1)); done; }; ssh-keygen -t rsa -f cluster -P '' -q -C cluster; mv cluster cluster.pem; mv cluster.* ~/.ssh; scan-hosts\"\n"
				+ "...expression:\" \"\n"
				+ "....conditionalExpression:null\n"
				+ ".....logicalORExpression:null\n"
				+ "......logicalANDExpression:null\n"
				+ ".......equalityExpression:null\n"
				+ "........relationalExpression:null\n"
				+ ".........additiveExpression:null\n"
				+ "..........multiplicativeExpression:null\n"
				+ "...........unaryExpression:null\n"
				+ "............functionExpression:\"string\"\n"
				+ ".............conditionalExpression:null\n"
				+ "..............logicalORExpression:null\n"
				+ "...............logicalANDExpression:null\n"
				+ "................equalityExpression:null\n"
				+ ".................relationalExpression:null\n"
				+ "..................additiveExpression:null\n"
				+ "...................multiplicativeExpression:null\n"
				+ "....................unaryExpression:null\n"
				+ ".....................identifier:\"DenoiseCluster.privateIpAddressList\"\n"
				+ ".............conditionalExpression:null\n"
				+ "..............logicalORExpression:null\n"
				+ "...............logicalANDExpression:null\n"
				+ "................equalityExpression:null\n"
				+ ".................relationalExpression:null\n"
				+ "..................additiveExpression:null\n"
				+ "...................multiplicativeExpression:null\n"
				+ "....................unaryExpression:null\n"
				+ ".....................constant:\" \"\n"
				+ ".............conditionalExpression:null\n"
				+ "..............logicalORExpression:null\n"
				+ "...............logicalANDExpression:null\n"
				+ "................equalityExpression:null\n"
				+ ".................relationalExpression:null\n"
				+ "..................additiveExpression:null\n"
				+ "...................multiplicativeExpression:null\n"
				+ "....................unaryExpression:null\n"
				+ ".....................constant:\"\"\n"
				+ "...passThru:\" >> ~/.ssh/known_hosts\n"
				+ " echo -n localhost,\"\n"
				+ "...expression:null\n"
				+ "....conditionalExpression:null\n"
				+ ".....logicalORExpression:null\n"
				+ "......logicalANDExpression:null\n"
				+ ".......equalityExpression:null\n"
				+ "........relationalExpression:null\n"
				+ ".........additiveExpression:null\n"
				+ "..........multiplicativeExpression:null\n"
				+ "...........unaryExpression:null\n"
				+ "............functionExpression:\"string\"\n"
				+ ".............conditionalExpression:null\n"
				+ "..............logicalORExpression:null\n"
				+ "...............logicalANDExpression:null\n"
				+ "................equalityExpression:null\n"
				+ ".................relationalExpression:null\n"
				+ "..................additiveExpression:null\n"
				+ "...................multiplicativeExpression:null\n"
				+ "....................unaryExpression:null\n"
				+ ".....................identifier:\"DenoiseCluster.privateIpAddressList\"\n"
				+ ".............conditionalExpression:null\n"
				+ "..............logicalORExpression:null\n"
				+ "...............logicalANDExpression:null\n"
				+ "................equalityExpression:null\n"
				+ ".................relationalExpression:null\n"
				+ "..................additiveExpression:null\n"
				+ "...................multiplicativeExpression:null\n"
				+ "....................unaryExpression:null\n"
				+ ".....................constant:\",\"\n"
				+ ".............conditionalExpression:null\n"
				+ "..............logicalORExpression:null\n"
				+ "...............logicalANDExpression:null\n"
				+ "................equalityExpression:null\n"
				+ ".................relationalExpression:null\n"
				+ "..................additiveExpression:null\n"
				+ "...................multiplicativeExpression:null\n"
				+ "....................unaryExpression:null\n"
				+ ".....................constant:\"\"\n"
				+ "...passThru:\", | sed 's/,/\\n/g' > ~/hosts\n"
				+ " CORES=8; echo Your workers have $CORES cores.\n"
				+ " n-times 3 echo localhost > ~/hosts.slave\n"
				+ " n-times $CORES sed 1d ~/hosts >>~/hosts.slave\n"
				+ " cat ~/.ssh/cluster.pub\n"
				+ " someother command --foobar <in >out.foo\n"
				+ "\"\n"
				+ ".forCommand:null\n"
				+ "..variable:\"$$i\"\n"
				+ "..expression:\" \"\n"
				+ "...conditionalExpression:null\n"
				+ "....logicalORExpression:null\n"
				+ ".....logicalANDExpression:null\n"
				+ "......equalityExpression:null\n"
				+ ".......relationalExpression:null\n"
				+ "........additiveExpression:\"-\"\n"
				+ ".........multiplicativeExpression:null\n"
				+ "..........unaryExpression:null\n"
				+ "...........identifier:\"n\"\n"
				+ ".........additiveExpression:null\n"
				+ "..........multiplicativeExpression:null\n"
				+ "...........unaryExpression:null\n"
				+ "............constant:1\n"
				+ "..block:null\n"
				+ "...on:null\n"
				+ "....expression:\" \"\n"
				+ ".....conditionalExpression:null\n"
				+ "......logicalORExpression:null\n"
				+ ".......logicalANDExpression:null\n"
				+ "........equalityExpression:null\n"
				+ ".........relationalExpression:null\n"
				+ "..........additiveExpression:null\n"
				+ "...........multiplicativeExpression:null\n"
				+ "............unaryExpression:null\n"
				+ ".............identifier:\"DenoiseCluster\"\n"
				+ ".............conditionalExpression:null\n"
				+ "..............logicalORExpression:null\n"
				+ "...............logicalANDExpression:null\n"
				+ "................equalityExpression:null\n"
				+ ".................relationalExpression:null\n"
				+ "..................additiveExpression:null\n"
				+ "...................multiplicativeExpression:null\n"
				+ "....................unaryExpression:null\n"
				+ ".....................identifier:\"i\"\n"
				+ "....pieces:null\n"
				+ ".....passThru:\"source /home/ubuntu/qiime_software/activate.sh\n"
				+ " cp $QIIME/qiime/support_files/qiime_config_n3phele ~/.qiime_config_default\n"
				+ " echo '\"\n"
				+ ".....expression:null\n"
				+ "......conditionalExpression:null\n"
				+ ".......logicalORExpression:null\n"
				+ "........logicalANDExpression:null\n"
				+ ".........equalityExpression:null\n"
				+ "..........relationalExpression:null\n"
				+ "...........additiveExpression:null\n"
				+ "............multiplicativeExpression:null\n"
				+ ".............unaryExpression:null\n"
				+ "..............functionExpression:\"regex\"\n"
				+ "...............conditionalExpression:null\n"
				+ "................logicalORExpression:null\n"
				+ ".................logicalANDExpression:null\n"
				+ "..................equalityExpression:null\n"
				+ "...................relationalExpression:null\n"
				+ "....................additiveExpression:null\n"
				+ ".....................multiplicativeExpression:null\n"
				+ "......................unaryExpression:null\n"
				+ ".......................identifier:\"setupMaster.stdout\"\n"
				+ "...............conditionalExpression:null\n"
				+ "................logicalORExpression:null\n"
				+ ".................logicalANDExpression:null\n"
				+ "..................equalityExpression:null\n"
				+ "...................relationalExpression:null\n"
				+ "....................additiveExpression:null\n"
				+ ".....................multiplicativeExpression:null\n"
				+ "......................unaryExpression:null\n"
				+ ".......................constant:\".*(ssh-rsa .*cluster).*\"\n"
				+ "...............conditionalExpression:null\n"
				+ "................logicalORExpression:null\n"
				+ ".................logicalANDExpression:null\n"
				+ "..................equalityExpression:null\n"
				+ "...................relationalExpression:null\n"
				+ "....................additiveExpression:null\n"
				+ ".....................multiplicativeExpression:null\n"
				+ "......................unaryExpression:null\n"
				+ ".......................constant:1\n"
				+ ".....passThru:\"' >> ~/.ssh/authorized_keys\n"
				+ " chmod 600 ~/.ssh/authorized_keys\n"
				+ "\"\n"
				+ ".on:null\n"
				+ "..expression:\" \"\n"
				+ "...conditionalExpression:null\n"
				+ "....logicalORExpression:null\n"
				+ ".....logicalANDExpression:null\n"
				+ "......equalityExpression:null\n"
				+ ".......relationalExpression:null\n"
				+ "........additiveExpression:null\n"
				+ ".........multiplicativeExpression:null\n"
				+ "..........unaryExpression:null\n"
				+ "...........identifier:\"DenoiseMaster\"\n"
				+ "..option:\"needsNone\"\n"
				+ "..option:\"producesNone\"\n"
				+ "..pieces:null\n"
				+ "...passThru:\"for i in `grep -v localhost ~/hosts.slave`; do ssh -i ~/.ssh/cluster.pem $i ping -q -c 3\"\n"
				+ "...expression:\" \"\n"
				+ "....conditionalExpression:null\n"
				+ ".....logicalORExpression:null\n"
				+ "......logicalANDExpression:null\n"
				+ ".......equalityExpression:null\n"
				+ "........relationalExpression:null\n"
				+ ".........additiveExpression:null\n"
				+ "..........multiplicativeExpression:null\n"
				+ "...........unaryExpression:null\n"
				+ "............identifier:\"DenoiseMaster.privateIpAddressList\"\n"
				+ "............conditionalExpression:null\n"
				+ ".............logicalORExpression:null\n"
				+ "..............logicalANDExpression:null\n"
				+ "...............equalityExpression:null\n"
				+ "................relationalExpression:null\n"
				+ ".................additiveExpression:null\n"
				+ "..................multiplicativeExpression:null\n"
				+ "...................unaryExpression:null\n"
				+ "....................constant:0\n"
				+ "...passThru:\" || echo $i failed; done\n"
				+ "\"\n"
				+ ".on:null\n"
				+ "..expression:\" \"\n"
				+ "...conditionalExpression:null\n"
				+ "....logicalORExpression:null\n"
				+ ".....logicalANDExpression:null\n"
				+ "......equalityExpression:null\n"
				+ ".......relationalExpression:null\n"
				+ "........additiveExpression:null\n"
				+ ".........multiplicativeExpression:null\n"
				+ "..........unaryExpression:null\n"
				+ "...........identifier:\"DenoiseMaster\"\n"
				+ "..option:\"produces\"\n"
				+ "...fileList:null\n"
				+ "....fileElement:\"denoiser.log:output/denoiser.log\"\n"
				+ "....fileElement:\"centroids.fasta:output/centroids.fasta\"\n"
				+ "....fileElement:\"singletons.fasta:output/singletons.fasta\"\n"
				+ "....fileElement:\"denoiser_mapping.txt:output/denoiser_mapping.txt\"\n"
				+ "..pieces:null\n"
				+ "...passThru:\"source /home/ubuntu/qiime_software/activate.sh\n"
				+ " cp $QIIME/qiime/support_files/qiime_config_n3phele ~/.qiime_config_default\n"
				+ " sed 's/127.0.1.1/#127.0.1.1/' </etc/hosts >/tmp/hosts\n"
				+ " cat /tmp/hosts >/etc/hosts\n"
				+ " shopt -s nullglob; files=\"flowgram.sff.txt\"\n"
				+ " for i in flowgram[1234].sff.txt; do files= $files, $i; done\n"
				+ " WORKERS=`wc -l < ~/hosts.slave`\n"
				+ " echo Run with $WORKERS workers.\n"
				+ " denoise_wrapper.py -v -i $files\"\n"
				+ "...expression:\" \"\n"
				+ "....conditionalExpression:null\n"
				+ ".....logicalORExpression:null\n"
				+ "......logicalANDExpression:null\n"
				+ ".......equalityExpression:\"==\"\n"
				+ "........relationalExpression:null\n"
				+ ".........additiveExpression:null\n"
				+ "..........multiplicativeExpression:null\n"
				+ "...........unaryExpression:null\n"
				+ "............identifier:\"primer\"\n"
				+ "........equalityExpression:null\n"
				+ ".........relationalExpression:null\n"
				+ "..........additiveExpression:null\n"
				+ "...........multiplicativeExpression:null\n"
				+ "............unaryExpression:null\n"
				+ ".............constant:\"LinkerPrimerSequence\"\n"
				+ ".....conditionalExpression:null\n"
				+ "......logicalORExpression:null\n"
				+ ".......logicalANDExpression:null\n"
				+ "........equalityExpression:null\n"
				+ ".........relationalExpression:null\n"
				+ "..........additiveExpression:null\n"
				+ "...........multiplicativeExpression:null\n"
				+ "............unaryExpression:null\n"
				+ ".............constant:\"-m mapping.txt \"\n"
				+ ".....conditionalExpression:null\n"
				+ "......logicalORExpression:null\n"
				+ ".......logicalANDExpression:null\n"
				+ "........equalityExpression:null\n"
				+ ".........relationalExpression:null\n"
				+ "..........additiveExpression:\"+\"\n"
				+ "...........multiplicativeExpression:null\n"
				+ "............unaryExpression:null\n"
				+ ".............constant:\"-p \"\n"
				+ "...........additiveExpression:null\n"
				+ "............multiplicativeExpression:null\n"
				+ ".............unaryExpression:null\n"
				+ "..............identifier:\"primer\"\n"
				+ "...passThru:\" -f seqs.fna -o output -n $WORKERS\"\n"
				+ "...expression:\" \"\n"
				+ "....conditionalExpression:null\n"
				+ ".....logicalORExpression:null\n"
				+ "......logicalANDExpression:null\n"
				+ ".......equalityExpression:null\n"
				+ "........relationalExpression:null\n"
				+ ".........additiveExpression:null\n"
				+ "..........multiplicativeExpression:null\n"
				+ "...........unaryExpression:null\n"
				+ "............identifier:\"titanium\"\n"
				+ ".....conditionalExpression:null\n"
				+ "......logicalORExpression:null\n"
				+ ".......logicalANDExpression:null\n"
				+ "........equalityExpression:null\n"
				+ ".........relationalExpression:null\n"
				+ "..........additiveExpression:null\n"
				+ "...........multiplicativeExpression:null\n"
				+ "............unaryExpression:null\n"
				+ ".............constant:\" --titanium \"\n"
				+ ".....conditionalExpression:null\n"
				+ "......logicalORExpression:null\n"
				+ ".......logicalANDExpression:null\n"
				+ "........equalityExpression:null\n"
				+ ".........relationalExpression:null\n"
				+ "..........additiveExpression:null\n"
				+ "...........multiplicativeExpression:null\n"
				+ "............unaryExpression:null\n"
				+ ".............constant:\"\"\n"
				+ "...passThru:\" || { tail -40 output/denoiser.log; exit 1; }\n"
				+ "\"\n"
				+ ".on:null\n"
				+ "..expression:\" \"\n"
				+ "...conditionalExpression:null\n"
				+ "....logicalORExpression:null\n"
				+ ".....logicalANDExpression:null\n"
				+ "......equalityExpression:null\n"
				+ ".......relationalExpression:null\n"
				+ "........additiveExpression:null\n"
				+ ".........multiplicativeExpression:null\n"
				+ "..........unaryExpression:null\n"
				+ "...........identifier:\"DenoiseMaster\"\n"
				+ "..option:\"needs\"\n"
				+ "...fileList:null\n"
				+ "....fileElement:\"sequence.fasta:sequence.fasta\"\n"
				+ "....fileElement:\"centroids.fasta:centroids.fasta\"\n"
				+ "....fileElement:\"singletons.fasta:singletons.fasta\"\n"
				+ "....fileElement:\"denoiser_mapping.txt:denoiser_mapping.txt\"\n"
				+ "..option:\"produces\"\n"
				+ "...literalArg:\"denoised.fasta\"\n"
				+ "..pieces:null\n"
				+ "...passThru:\"source /home/ubuntu/qiime_software/activate.sh\n"
				+ " inflate_denoiser_output.py -v -c output/centroids.fasta -s output/singletons.fasta -f seqs.fna -d output/denoiser_mapping.txt -o denoised.fasta\n"
				+ "\"\n";
		
		Assert.assertEquals(expected, result);
	}
	
	@Test
	public void denoiseCompileTest() throws FileNotFoundException, ParseException, Exception {

		NParser n = new NParser(new FileInputStream("./test/denoiseTest.n"));
		CommandDefinition cd = n.parse();

		//
		//	Shell parsing
		//
		Shell s = new Shell(cd.getImplementations().get(0).getBody(), cd.getImplementations().get(0).getLineNo());
		cd.getImplementations().get(0).setCompiled(s.compile());
		SelfCompilingNode node = new Shell(cd.getImplementations().get(0).getBody(), cd.getImplementations().get(0).getLineNo()).script();
		
		String result = dumpCompiled(cd.getImplementations().get(0));
		String expected = "script:null\n"
				+ ".variableAssign:DenoiseCluster\n"
				+ "..createvm:null\n"
				+ "...option:name\n"
				+ "....literalArg:DenoiseCluster\n"
				+ "...option:imageId\n"
				+ "....literalArg:ami-e4bf1b8d\n"
				+ "...option:minCount\n"
				+ "....expression: \n"
				+ ".....additiveExpression:-\n"
				+ "......identifier:n\n"
				+ "......constantLong:1\n"
				+ "...option:spotPrice\n"
				+ "....expression: \n"
				+ ".....identifier:spotPrice\n"
				+ "...option:instanceType\n"
				+ "....literalArg:c1.xlarge\n"
				+ "...option:securityGroups\n"
				+ "....literalArg:n3phele-default\n"
				+ "...option:userData\n"
				+ "....literalArg:#!/bin/bash\n"
				+ "chmod a+rwx /mnt\n"
				+ "set -x\n"
				+ "wget -q -O - https://s3.amazonaws.com/n3phele-agent/addswap | /bin/bash -s 2000\n"
				+ "wget -q -O - https://n3phele-agent.s3.amazonaws.com/n3ph-install-tgz-basic | su - -c '/bin/bash -s ubuntu ~/agent /mnt/sandbox' ubuntu\n"
				+ "ln -s /mnt/sandbox ~ubuntu/sandbox\n"
				+ "\n"
				+ ".createvm:null\n"
				+ "..option:name\n"
				+ "...literalArg:DenoiseMaster\n"
				+ "..option:imageId\n"
				+ "...literalArg:ami-e4bf1b8d\n"
				+ "..option:launchGroup\n"
				+ "...literalArg:denoise\n"
				+ "..option:minCount\n"
				+ "...literalArg:1\n"
				+ "..option:spotPrice\n"
				+ "...expression: \n"
				+ "....identifier:spotPrice\n"
				+ "..option:instanceType\n"
				+ "...literalArg:m1.xlarge\n"
				+ "..option:securityGroups\n"
				+ "...literalArg:n3phele-default\n"
				+ "..option:userData\n"
				+ "...literalArg:#!/bin/bash\n"
				+ "chmod a+rwx /mnt\n"
				+ "set -x\n"
				+ "wget -q -O - https://s3.amazonaws.com/n3phele-agent/addswap | /bin/bash -s 2000\n"
				+ "wget -q -O - https://n3phele-agent.s3.amazonaws.com/n3ph-install-tgz-basic | su - -c '/bin/bash -s ubuntu ~/agent /mnt/sandbox' ubuntu\n"
				+ "ln -s /mnt/sandbox ~ubuntu/sandbox\n"
				+ "\n"
				+ ".on:null\n"
				+ "..expression: \n"
				+ "...identifier:DenoiseMaster\n"
				+ "..option:needsAll\n"
				+ "..option:producesNone\n"
				+ "..pieces:null\n"
				+ "...passThru:function scan-hosts() { i=0; while [ $i -le 10 ]; do i=$(( $i+1)); ssh-keyscan -H $* > /tmp/known_hosts; if [\"`wc -l </tmp/known_hosts`\" -ne \"$#\" ]; then echo not done .. retrying >&2; sleep 5;else cat /tmp/known_hosts; break; fi; done };\n"
				+ " function n-times() { i=0; n=$1; shift; while [ $i -lt $n ]; do $*; i=$(( $i+1)); done; }; ssh-keygen -t rsa -f cluster -P '' -q -C cluster; mv cluster cluster.pem; mv cluster.* ~/.ssh; scan-hosts\n"
				+ "...expression: \n"
				+ "....functionExpression:string\n"
				+ ".....identifier:DenoiseCluster.privateIpAddressList\n"
				+ ".....constantString: \n"
				+ ".....constantString:\n"
				+ "...passThru: >> ~/.ssh/known_hosts\n"
				+ " echo -n localhost,\n"
				+ "...expression:null\n"
				+ "....functionExpression:string\n"
				+ ".....identifier:DenoiseCluster.privateIpAddressList\n"
				+ ".....constantString:,\n"
				+ ".....constantString:\n"
				+ "...passThru:, | sed 's/,/\\n/g' > ~/hosts\n"
				+ " CORES=8; echo Your workers have $CORES cores.\n"
				+ " n-times 3 echo localhost > ~/hosts.slave\n"
				+ " n-times $CORES sed 1d ~/hosts >>~/hosts.slave\n"
				+ " cat ~/.ssh/cluster.pub\n"
				+ " someother command --foobar <in >out.foo\n"
				+ "\n"
				+ ".forCommand:null\n"
				+ "..variable:$$i\n"
				+ "..expression: \n"
				+ "...additiveExpression:-\n"
				+ "....identifier:n\n"
				+ "....constantLong:1\n"
				+ "..block:null\n"
				+ "...on:null\n"
				+ "....expression: \n"
				+ ".....unaryExpression:null\n"
				+ "......identifier:DenoiseCluster\n"
				+ "......identifier:i\n"
				+ "....pieces:null\n"
				+ ".....passThru:source /home/ubuntu/qiime_software/activate.sh\n"
				+ " cp $QIIME/qiime/support_files/qiime_config_n3phele ~/.qiime_config_default\n"
				+ " echo '\n"
				+ ".....expression:null\n"
				+ "......functionExpression:regex\n"
				+ ".......identifier:setupMaster.stdout\n"
				+ ".......constantString:.*(ssh-rsa .*cluster).*\n"
				+ ".......constantLong:1\n"
				+ ".....passThru:' >> ~/.ssh/authorized_keys\n"
				+ " chmod 600 ~/.ssh/authorized_keys\n"
				+ "\n"
				+ ".on:null\n"
				+ "..expression: \n"
				+ "...identifier:DenoiseMaster\n"
				+ "..option:needsNone\n"
				+ "..option:producesNone\n"
				+ "..pieces:null\n"
				+ "...passThru:for i in `grep -v localhost ~/hosts.slave`; do ssh -i ~/.ssh/cluster.pem $i ping -q -c 3\n"
				+ "...expression: \n"
				+ "....unaryExpression:null\n"
				+ ".....identifier:DenoiseMaster.privateIpAddressList\n"
				+ ".....constantLong:0\n"
				+ "...passThru: || echo $i failed; done\n"
				+ "\n"
				+ ".on:null\n"
				+ "..expression: \n"
				+ "...identifier:DenoiseMaster\n"
				+ "..option:produces\n"
				+ "...fileList:null\n"
				+ "....fileElement:denoiser.log:output/denoiser.log\n"
				+ "....fileElement:centroids.fasta:output/centroids.fasta\n"
				+ "....fileElement:singletons.fasta:output/singletons.fasta\n"
				+ "....fileElement:denoiser_mapping.txt:output/denoiser_mapping.txt\n"
				+ "..pieces:null\n"
				+ "...passThru:source /home/ubuntu/qiime_software/activate.sh\n"
				+ " cp $QIIME/qiime/support_files/qiime_config_n3phele ~/.qiime_config_default\n"
				+ " sed 's/127.0.1.1/#127.0.1.1/' </etc/hosts >/tmp/hosts\n"
				+ " cat /tmp/hosts >/etc/hosts\n"
				+ " shopt -s nullglob; files=\"flowgram.sff.txt\"\n"
				+ " for i in flowgram[1234].sff.txt; do files= $files, $i; done\n"
				+ " WORKERS=`wc -l < ~/hosts.slave`\n"
				+ " echo Run with $WORKERS workers.\n"
				+ " denoise_wrapper.py -v -i $files\n"
				+ "...expression: \n"
				+ "....conditionalExpression:null\n"
				+ ".....equalityExpression:==\n"
				+ "......identifier:primer\n"
				+ "......constantString:LinkerPrimerSequence\n"
				+ ".....constantString:-m mapping.txt \n"
				+ ".....additiveExpression:+\n"
				+ "......constantString:-p \n"
				+ "......identifier:primer\n"
				+ "...passThru: -f seqs.fna -o output -n $WORKERS\n"
				+ "...expression: \n"
				+ "....conditionalExpression:null\n"
				+ ".....identifier:titanium\n"
				+ ".....constantString: --titanium \n"
				+ ".....constantString:\n"
				+ "...passThru: || { tail -40 output/denoiser.log; exit 1; }\n"
				+ "\n"
				+ ".on:null\n"
				+ "..expression: \n"
				+ "...identifier:DenoiseMaster\n"
				+ "..option:needs\n"
				+ "...fileList:null\n"
				+ "....fileElement:sequence.fasta:sequence.fasta\n"
				+ "....fileElement:centroids.fasta:centroids.fasta\n"
				+ "....fileElement:singletons.fasta:singletons.fasta\n"
				+ "....fileElement:denoiser_mapping.txt:denoiser_mapping.txt\n"
				+ "..option:produces\n"
				+ "...literalArg:denoised.fasta\n"
				+ "..pieces:null\n"
				+ "...passThru:source /home/ubuntu/qiime_software/activate.sh\n"
				+ " inflate_denoiser_output.py -v -c output/centroids.fasta -s output/singletons.fasta -f seqs.fna -d output/denoiser_mapping.txt -o denoised.fasta\n"
				+ "\n"
				+ "";

		Assert.assertEquals(expected, result);
	}
	
	private String dump(SimpleNode node, String prefix) {
		String result = node.toString(prefix)+"\n";
	    if (node.jjtGetNumChildren() != 0) {
	      for (int i = 0; i < (node.jjtGetNumChildren()); ++i) {
	        SimpleNode n = (SimpleNode)node.jjtGetChild(i);
	        if (n != null) {
	          result += dump(n, prefix + " ");
	        }
	      }
	    }
	    return result;
	}
	
	private String dump(SimpleNode node, String prefix, String fill) {
		String result = node.toString(prefix)+"\n";
	    if (node.jjtGetNumChildren() != 0) {
	      for (int i = 0; i < (node.jjtGetNumChildren()); ++i) {
	        SimpleNode n = (SimpleNode)node.jjtGetChild(i);
	        if (n != null) {
	          result += dump(n, prefix + fill, fill);
	        }
	      }
	    }
	    return result;
	}
	
	private String dumpCompiled(CommandImplementationDefinition cid) {
		List<ShellFragment> sf = cid.getCompiled();
		String result = dumpCompiled(sf, sf.size()-1, "", ".");
		
	    return result;
	}
	
	
	private String dumpCompiled(List<ShellFragment> list, int index, String prefix, String fill) {
		ShellFragment sf = list.get(index);
		String result = prefix+sf.kind.toString()+":"+sf.value+"\n";
	    if (sf.children != null) {
	      for (int i = 0; i < (sf.children.length); ++i) {
	          result += dumpCompiled(list, sf.children[i], prefix + fill, fill);
	      }
	    }
	    return result;
	}
	private void transform(String result) {
		StringBuilder b = new StringBuilder();
		for(int i = 0; i < result.length(); i++) {
			char c = result.charAt(i);
			if(c == '\n') {
				b.append("\\n\"\n	+ \"");
			} else if(c == '\r') {
				b.append("\\r");
			} else if(c == '\t') {
				b.append("\\t");
			} else if(c == '\\') {
				b.append("\\\\");
			} else if(c == '"') {
				b.append("\\\"");
			} else {
				b.append(c);
			}
			
		}
		System.out.println("String expected = \""+b.toString()+"\";");
	}
	
	/** Expression exception handling, including report of line, column and offending text
	 * @throws FileNotFoundException
	 * @throws n3phele.service.nShell.ParseException
	 * @throws n3phele.service.nShell.ParseException 
	 */
	@Test
	public void exceptionTest1() throws FileNotFoundException, ParseException {
		NParser n = new NParser(new FileInputStream("./test/denoiseTestException1.n"));


		try {
		 CommandDefinition cd = n.parse();
			Shell s = new Shell(cd.getImplementations().get(0).getBody(), cd.getImplementations().get(0).getLineNo());
			SimpleNode node = s.script();
			fail("Exception expected");
		} catch (ParseException p) {
			Assert.assertEquals(69, p.currentToken.beginLine);
			Assert.assertEquals(20, p.currentToken.beginColumn);
			Assert.assertEquals("Unexpected", ">", p.currentToken.image);
		}
	}
	
	/** Test simple ON statement
	 * @throws ParseException
	 * @throws n3phele.service.nShell.ParseException 
	 */
	@Test
	public void shellTest_ON_1() throws ParseException, n3phele.service.nShell.ParseException {
		String test = "  ON $$DenoiseMaster --needsAll --producesNone\n"+
					  "    ls -lx | sort | cat > /tmp/foo.bar\n";
		Shell s = new Shell(test, 1);
		SimpleNode node = s.script();
		String result = dump(node, "", ".");
		String expected = "script:null\n"
				+ ".on:null\n"
				+ "..expression:\" \"\n"
				+ "...conditionalExpression:null\n"
				+ "....logicalORExpression:null\n"
				+ ".....logicalANDExpression:null\n"
				+ "......equalityExpression:null\n"
				+ ".......relationalExpression:null\n"
				+ "........additiveExpression:null\n"
				+ ".........multiplicativeExpression:null\n"
				+ "..........unaryExpression:null\n"
				+ "...........identifier:\"DenoiseMaster\"\n"
				+ "..option:\"needsAll\"\n"
				+ "..option:\"producesNone\"\n"
				+ "..pieces:null\n"
				+ "...passThru:\"ls -lx | sort | cat > /tmp/foo.bar\n"
				+ "\"\n"
				+ "";
		Assert.assertEquals(expected, result);

		
	}
	
	/** Tests the processing of a file list with both default and explicitly specified filenames.
	 * @throws ParseException
	 * @throws n3phele.service.nShell.ParseException 
	 */
	@Test
	public void shellTest_ON_2() throws ParseException, n3phele.service.nShell.ParseException {
		String test = 	"    ON $$DenoiseMaster --produces [denoiser.log,\n"+
						"    				centroids.fasta: output/centroids.fasta,\n"+
						"    			    singletons.fasta:output/singletons.fasta,\n"+
						"    			    denoiser_mapping.txt: output/denoiser_mapping.txt]\n"+
						"	    	cp $QIIME/qiime/support_files/qiime_config_n3phele ~/.qiime_config_default\n";
		Shell s = new Shell(test, 1);
		SimpleNode node = s.script();
		String result = dump(node, "", ".");
		String expected = "script:null\n"
				+ ".on:null\n"
				+ "..expression:\" \"\n"
				+ "...conditionalExpression:null\n"
				+ "....logicalORExpression:null\n"
				+ ".....logicalANDExpression:null\n"
				+ "......equalityExpression:null\n"
				+ ".......relationalExpression:null\n"
				+ "........additiveExpression:null\n"
				+ ".........multiplicativeExpression:null\n"
				+ "..........unaryExpression:null\n"
				+ "...........identifier:\"DenoiseMaster\"\n"
				+ "..option:\"produces\"\n"
				+ "...fileList:null\n"
				+ "....fileElement:\"denoiser.log:denoiser.log\"\n"
				+ "....fileElement:\"centroids.fasta:output/centroids.fasta\"\n"
				+ "....fileElement:\"singletons.fasta:output/singletons.fasta\"\n"
				+ "....fileElement:\"denoiser_mapping.txt:output/denoiser_mapping.txt\"\n"
				+ "..pieces:null\n"
				+ "...passThru:\"cp $QIIME/qiime/support_files/qiime_config_n3phele ~/.qiime_config_default\n"
				+ "\"\n"
				+ "";

		Assert.assertEquals(expected, result);

		
	}
	
	
	/** Test FOR statement with multiple statements defined in the FOR block and a statement following the FOR block
	 * @throws ParseException
	 * @throws n3phele.service.nShell.ParseException 
	 */
	@Test
	public void shellTest_FOR_1() throws ParseException, n3phele.service.nShell.ParseException {
		String test = "	FOR $$i : $$n-1\n" +
					  "		ON $$DenoiseCluster[$$i]\n" +
					  "			first --command\n" +
					  "		ON $$DenoiseCluster[$$i]\n" +
					  "			second -help\n" +
					  "	ON $$DenoiseMaster\n" +
					  "			third foobar > file.txt\n";
		Shell s = new Shell(test, 1);
		SimpleNode node = s.script();
		String result = dump(node, "", ".");
		String expected = "script:null\n"
				+ ".forCommand:null\n"
				+ "..variable:\"$$i\"\n"
				+ "..expression:\" \"\n"
				+ "...conditionalExpression:null\n"
				+ "....logicalORExpression:null\n"
				+ ".....logicalANDExpression:null\n"
				+ "......equalityExpression:null\n"
				+ ".......relationalExpression:null\n"
				+ "........additiveExpression:\"-\"\n"
				+ ".........multiplicativeExpression:null\n"
				+ "..........unaryExpression:null\n"
				+ "...........identifier:\"n\"\n"
				+ ".........additiveExpression:null\n"
				+ "..........multiplicativeExpression:null\n"
				+ "...........unaryExpression:null\n"
				+ "............constant:1\n"
				+ "..block:null\n"
				+ "...on:null\n"
				+ "....expression:\" \"\n"
				+ ".....conditionalExpression:null\n"
				+ "......logicalORExpression:null\n"
				+ ".......logicalANDExpression:null\n"
				+ "........equalityExpression:null\n"
				+ ".........relationalExpression:null\n"
				+ "..........additiveExpression:null\n"
				+ "...........multiplicativeExpression:null\n"
				+ "............unaryExpression:null\n"
				+ ".............identifier:\"DenoiseCluster\"\n"
				+ ".............conditionalExpression:null\n"
				+ "..............logicalORExpression:null\n"
				+ "...............logicalANDExpression:null\n"
				+ "................equalityExpression:null\n"
				+ ".................relationalExpression:null\n"
				+ "..................additiveExpression:null\n"
				+ "...................multiplicativeExpression:null\n"
				+ "....................unaryExpression:null\n"
				+ ".....................identifier:\"i\"\n"
				+ "....pieces:null\n"
				+ ".....passThru:\"first --command\n"
				+ "\"\n"
				+ "...on:null\n"
				+ "....expression:\" \"\n"
				+ ".....conditionalExpression:null\n"
				+ "......logicalORExpression:null\n"
				+ ".......logicalANDExpression:null\n"
				+ "........equalityExpression:null\n"
				+ ".........relationalExpression:null\n"
				+ "..........additiveExpression:null\n"
				+ "...........multiplicativeExpression:null\n"
				+ "............unaryExpression:null\n"
				+ ".............identifier:\"DenoiseCluster\"\n"
				+ ".............conditionalExpression:null\n"
				+ "..............logicalORExpression:null\n"
				+ "...............logicalANDExpression:null\n"
				+ "................equalityExpression:null\n"
				+ ".................relationalExpression:null\n"
				+ "..................additiveExpression:null\n"
				+ "...................multiplicativeExpression:null\n"
				+ "....................unaryExpression:null\n"
				+ ".....................identifier:\"i\"\n"
				+ "....pieces:null\n"
				+ ".....passThru:\"second -help\n"
				+ "\"\n"
				+ ".on:null\n"
				+ "..expression:\" \"\n"
				+ "...conditionalExpression:null\n"
				+ "....logicalORExpression:null\n"
				+ ".....logicalANDExpression:null\n"
				+ "......equalityExpression:null\n"
				+ ".......relationalExpression:null\n"
				+ "........additiveExpression:null\n"
				+ ".........multiplicativeExpression:null\n"
				+ "..........unaryExpression:null\n"
				+ "...........identifier:\"DenoiseMaster\"\n"
				+ "..pieces:null\n"
				+ "...passThru:\"third foobar > file.txt\n"
				+ "\"\n";
		Assert.assertEquals(expected, result);	
	}
	
	/** Tests multi-line CREATEVM statement with a multi-line literal for userdata. Multi-line literal has leading whitespace removal.
	 * @throws ParseException
	 * @throws n3phele.service.nShell.ParseException 
	 */
	@Test
	public void shellTest_CREATEVM_1() throws ParseException, n3phele.service.nShell.ParseException {
		String test = "	CREATEVM --name DenoiseMaster --imageId ami-e4bf1b8d --launchGroup \"denoise\" --minCount 1 --spotPrice $$spotPrice\n"+
						"	 --instanceType m1.xlarge --securityGroups n3phele-default\n"+
						"	 --userData %%{\n"+
						"		#!/bin/bash\n"+
						"		chmod a+rwx /mnt\n"+
						"		set -x\n"+
						"		wget -q -O - https://s3.amazonaws.com/n3phele-agent/addswap | /bin/bash -s 2000\n"+
						"		wget -q -O - https://n3phele-agent.s3.amazonaws.com/n3ph-install-tgz-basic | su - -c '/bin/bash -s ubuntu ~/agent /mnt/sandbox' ubuntu\n"+
						"		ln -s /mnt/sandbox ~ubuntu/sandbox\n"+
						"	 }%%\n";
		Shell s = new Shell(test, 1);
		SimpleNode node = s.script();
		String result = dump(node, "", ".");
		String expected = "script:null\n"
				+ ".createvm:null\n"
				+ "..option:\"name\"\n"
				+ "...literalArg:\"DenoiseMaster\"\n"
				+ "..option:\"imageId\"\n"
				+ "...literalArg:\"ami-e4bf1b8d\"\n"
				+ "..option:\"launchGroup\"\n"
				+ "...literalArg:\"denoise\"\n"
				+ "..option:\"minCount\"\n"
				+ "...literalArg:\"1\"\n"
				+ "..option:\"spotPrice\"\n"
				+ "...expression:\" \"\n"
				+ "....conditionalExpression:null\n"
				+ ".....logicalORExpression:null\n"
				+ "......logicalANDExpression:null\n"
				+ ".......equalityExpression:null\n"
				+ "........relationalExpression:null\n"
				+ ".........additiveExpression:null\n"
				+ "..........multiplicativeExpression:null\n"
				+ "...........unaryExpression:null\n"
				+ "............identifier:\"spotPrice\"\n"
				+ "..option:\"instanceType\"\n"
				+ "...literalArg:\"m1.xlarge\"\n"
				+ "..option:\"securityGroups\"\n"
				+ "...literalArg:\"n3phele-default\"\n"
				+ "..option:\"userData\"\n"
				+ "...literalArg:\"#!/bin/bash\n"
				+ "chmod a+rwx /mnt\n"
				+ "set -x\n"
				+ "wget -q -O - https://s3.amazonaws.com/n3phele-agent/addswap | /bin/bash -s 2000\n"
				+ "wget -q -O - https://n3phele-agent.s3.amazonaws.com/n3ph-install-tgz-basic | su - -c '/bin/bash -s ubuntu ~/agent /mnt/sandbox' ubuntu\n"
				+ "ln -s /mnt/sandbox ~ubuntu/sandbox\n"
				+ "\"\n"
				+ "";
		Assert.assertEquals(expected, result);
	}
	
	@Test
	public void shellTest_LOG() throws ParseException, n3phele.service.nShell.ParseException, FileNotFoundException {
		NParser n = new NParser(new FileInputStream("./test/doubleLogTest.n"));
		CommandDefinition cd = n.parse();
		Assert.assertEquals("doubleLog", cd.getName());
		Assert.assertEquals("produce a log message with a suffix", cd.getDescription());
		Assert.assertTrue(cd.isPublic());
		Assert.assertTrue(cd.isPreferred());
		Assert.assertEquals("1.0", cd.getVersion());
		Shell s = new Shell(cd.getImplementations().get(0).getBody(), cd.getImplementations().get(0).getLineNo());
		SimpleNode node = s.script();
		String result = dump(node, "", ".");
		String expected = "script:null\n"
				+ ".variableAssign:\"log\"\n"
				+ "..log:null\n"
				+ "...pieces:null\n"
				+ "....expression:\" \"\n"
				+ ".....conditionalExpression:null\n"
				+ "......logicalORExpression:null\n"
				+ ".......logicalANDExpression:null\n"
				+ "........equalityExpression:null\n"
				+ ".........relationalExpression:null\n"
				+ "..........additiveExpression:null\n"
				+ "...........multiplicativeExpression:null\n"
				+ "............unaryExpression:null\n"
				+ ".............identifier:\"message\"\n"
				+ ".log:null\n"
				+ "..pieces:null\n"
				+ "...expression:\"  \"\n"
				+ "....conditionalExpression:null\n"
				+ ".....logicalORExpression:null\n"
				+ "......logicalANDExpression:null\n"
				+ ".......equalityExpression:null\n"
				+ "........relationalExpression:null\n"
				+ ".........additiveExpression:null\n"
				+ "..........multiplicativeExpression:null\n"
				+ "...........unaryExpression:null\n"
				+ "............identifier:\"log.message\"\n"
				+ "...passThru:\" and a suffix\n"
				+ "\"\n";


		Assert.assertEquals(expected, result);
	}
	
	@Test
	public void shellTest_ON() throws ParseException, n3phele.service.nShell.ParseException, FileNotFoundException {
		NParser n = new NParser(new FileInputStream("./test/onCommandSingleOutputFileTest.n"));
		CommandDefinition cd = n.parse();
		Assert.assertEquals("onCommandSingleOutputFile", cd.getName());
		Assert.assertEquals("run a command that has a single input and file", cd.getDescription());
		Assert.assertTrue(cd.isPublic());
		Assert.assertTrue(cd.isPreferred());
		Assert.assertEquals("1.1", cd.getVersion());
		Shell s = new Shell(cd.getImplementations().get(0).getBody(), cd.getImplementations().get(0).getLineNo());
		SimpleNode node = s.script();
		String result = dump(node, "", ".");
		String expected = "script:null\n"
				+ ".variableAssign:\"my_vm\"\n"
				+ "..createvm:null\n"
				+ "...option:\"account\"\n"
				+ "....expression:\" \"\n"
				+ ".....conditionalExpression:null\n"
				+ "......logicalORExpression:null\n"
				+ ".......logicalANDExpression:null\n"
				+ "........equalityExpression:null\n"
				+ ".........relationalExpression:null\n"
				+ "..........additiveExpression:null\n"
				+ "...........multiplicativeExpression:null\n"
				+ "............unaryExpression:null\n"
				+ ".............identifier:\"account\"\n"
				+ ".on:null\n"
				+ "..expression:\" \"\n"
				+ "...conditionalExpression:null\n"
				+ "....logicalORExpression:null\n"
				+ ".....logicalANDExpression:null\n"
				+ "......equalityExpression:null\n"
				+ ".......relationalExpression:null\n"
				+ "........additiveExpression:null\n"
				+ ".........multiplicativeExpression:null\n"
				+ "..........unaryExpression:null\n"
				+ "...........identifier:\"my_vm\"\n"
				+ "..option:\"produces\"\n"
				+ "...fileList:null\n"
				+ "....fileElement:\"denoiser.log:output/denoiser.log\"\n"
				+ "..pieces:null\n"
				+ "...passThru:\"mkdir output; cat < flowgram.sff.txt | wc -l > output/denoiser.log\n"
				+ "\"\n"
				+ "";
		Assert.assertEquals(expected, result);
		
		//transform(dump(node, "", "."));
		String resultCompiled = dumpCompiled(cd.getImplementations().get(0));
		String expectedCompiled = "script:null\n"
				+ ".variableAssign:my_vm\n"
				+ "..createvm:null\n"
				+ "...option:account\n"
				+ "....expression: \n"
				+ ".....identifier:account\n"
				+ ".on:null\n"
				+ "..expression: \n"
				+ "...identifier:my_vm\n"
				+ "..option:produces\n"
				+ "...fileList:null\n"
				+ "....fileElement:denoiser.log:output/denoiser.log\n"
				+ "..pieces:null\n"
				+ "...passThru:mkdir output; cat < flowgram.sff.txt | wc -l > output/denoiser.log\n"
				+ "\n"
				+ "";
		Assert.assertEquals(expectedCompiled, resultCompiled);
	}
}
