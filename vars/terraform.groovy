//Terraform Utils
//Works only within a "script" block

//Works only on bash/sh Linux based agents / docker agents based on linux with bash/sh
//(And of course - with terraform binary installed there...)

/**
 * Simply does terraform init
 * (Must be in the relevant terraform directory first)
 * 
 * @param String stateName (requiered)
 * @return void
 *
 * @usage examples: 
 *        terraform.init(stateName: 'key=my-terraform-state')
 */
def init(String stateName) {
  sh "rm -f .terraform/terraform.tfstate"
  sh "terraform init -no-color --backend-config=${stateName}"
}

def init(Map config) {
  if (!config.containsKey('stateName')) {
    throw new Exception("Method 'terraform.init' must contain param 'stateName'!") 
  }
  init(config.stateName)
}


/**
 * Simply checks if a resource existing within the terraform state
 * (Must be in the relevant terraform directory first, and terraform init first)
 * 
 * @param String resourceName (requiered)
 * @return boolean
 *
 * @usage examples: 
 *        terraform.isResourceExist(resourceName: 'aws_vpc.my_vpc')
 *        terraform.isResourceExist(resourceName: 'aws_key_pair.generated_key[0]')
 */

def isResourceExist(String resourceName) {
  isResourceExist = sh(returnStdout: true, script: "terraform state list ${resourceName} || true").trim()
  return isResourceExist?.trim()
}

def isResourceExist(Map config) {
  if (!config.containsKey('resourceName')) {
    throw new Exception("Method 'terraform.isResourceExist' must contain param 'resourceName'!") 
  }
  return isResourceExist(config.resourceName)
}


/**
 * Returns the ID of and AWS VPC from a terraform resource 'aws_vpc' 
 * (Must be in the relevant terraform directory first, and terraform init first)
 * 
 * @param String vpcResourceName (requiered)
 * @return String
 *
 * @usage examples:
 *        def vpc_id = terraform.getAwsVpcId(vpcResourceName: 'aws_vpc.my_vpc')
 *        def vpc_id = terraform.getAwsVpcId(vpcResourceName: 'my_vpc')        //for aws_vpc.my_key
 *        def vpc_id = terraform.getAwsVpcId(vpcResourceName: 'aws_vpc.multi_vpc[0]')
 *        def vpc_id = terraform.getAwsVpcId(vpcResourceName: 'multi_vpc[0]') //for aws_vpc.multi_vpc[0]
 */

def getAwsVpcId(String vpcResourceName) {
  rName = vpcResourceName.minus("aws_vpc.") //Just in case...
  vpc_id = sh(returnStdout: true, script: "terraform state show aws_vpc.${rName} | grep id | grep vpc- | awk -F '\"' '{print \$2}'").trim()
  return vpc_id
}

def getAwsVpcId(Map config) {
  if (!config.containsKey('vpcResourceName')) {
    throw new Exception("Method 'terraform.getAwsVpcId' must contain param 'vpcResourceName'!") 
  }
  return getAwsVpcId(config.vpcResourceName)
}


/**
 * Returns the name (ID) of AWS Key Pair from a terraform resource 'aws_key_pair' 
 * (Must be in the relevant terraform directory first, and terraform init first)
 * 
 * @param String keyResourceName (requiered)
 * @return String
 *
 * @usage examples:
 *        def key_name = terraform.getAwsKeyPairName(keyResourceName: 'aws_key_pair.my_key')
 *        def key_name = terraform.getAwsKeyPairName(keyResourceName: 'my_key')       //for aws_key_pair.my_key
 *        def key_name = terraform.getAwsKeyPairName(keyResourceName: 'aws_key_pair.multi_key[0]')
 *        def key_name = terraform.getAwsKeyPairName(keyResourceName: 'multi_key[0]') //for aws_key_pair.multi_key[0]
 */

def getAwsKeyPairName(String keyResourceName) {
  rName = keyResourceName.minus("aws_key_pair.") //Just in case...
  key_name = sh(returnStdout: true, script: "terraform state show aws_key_pair.${rName} | grep key_name | awk -F '\"' '{print \$2}'").trim()
  return key_name
}

def getAwsKeyPairName(Map config) {
  if (!config.containsKey('keyResourceName')) {
    throw new Exception("Method 'terraform.getAwsKeyPairName' must contain param 'keyResourceName'!") 
  }
  return getAwsKeyPairName(config.keyResourceName)
}


/**
 * Does terraform destroy, excluding the list of resources provided in parameter.
 * (Must be in the relevant terraform directory first)
 * 
 * @param ArrayList excludeList (requiered, list of strings) - can be full or partial name of resources, see examples below
 * @param ArrayList varList (optional, list of strings)
 * @return void
 *
 * @usage examples: 
 *        terraform.destroyExclude(excludeList: ['vpc', 'subnet'])
 *        terraform.destroyExclude(excludeList: ['aws_vpc.my-vpc', 'aws_subnet'])
 *        terraform.destroyExclude(excludeList: ['vpc', 'subnet'], varList: ['deploy_region=us-east-1', 'cluster_name=my-cluster'])
 */

def destroyExclude(ArrayList excludeList = [], ArrayList varList = []) {
  if (excludeList.size() == 0) {
    throw new Exception("excludeList must contain at list 1 exclude element!")
  }

  exclude_list_str = ""
  excludeList.each {
    exclude_list_str = "${exclude_list_str}${it}\\|"
  }
  exclude_list_str = "${exclude_list_str}data\\."

  var_list_str = ""
  varList.each {
    var_list_str = "${var_list_str} -var ${it}"
  }
  var_list_str = var_list_str.trim()
  sh """
     DESTROY_PARAMS=""
     while read line; do
       DESTROY_PARAMS="\$DESTROY_PARAMS -target \$line"
     done < <(terraform state list | grep -v '${exclude_list_str}')

     if [[ -n \$DESTROY_PARAMS ]]; then
       terraform destroy -no-color -auto-approve ${var_list_str} \$DESTROY_PARAMS
     else
       echo No resources left to destroy here.
     fi
  """
}

def destroyExclude(Map config) {
  if (!config.containsKey('excludeList')) {
    throw new Exception("Method 'terraform.destroyExclude' must contain param 'excludeList'!") 
  }
  if (!config.containsKey('varList')) { config.varList = [] }
  destroyExclude(config.excludeList, config.varList)
}
