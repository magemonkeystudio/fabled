import re
import sys

is_dev = len(sys.argv) >= 2 and eval(sys.argv[1].lower().capitalize())
prep = not is_dev and len(sys.argv) >= 3 and bool(sys.argv[2])


def replace_version():
    regex = r'^[ ]{4}<version>((\d+\.?)+)((-pre)?-SNAPSHOT)?<\/version>$'
    with open('pom.xml', 'r') as pom:
        contents = pom.read()
        ver = re.findall(regex, contents, re.MULTILINE)
        version = ver[0][0]
        patch = int(ver[0][1])
        if is_dev:
            if len(version.split('.')) == 3:
                new_version = version + ".0-pre-SNAPSHOT"
            else:
                if 'SNAPSHOT' in ver[0][2]:
                    patch += 1
                new_version = version[:-(len(str(patch)))] + str(patch) + '-pre-SNAPSHOT'
        elif prep:
            new_version = version[:-(len(str(patch)))] + '0'
            print(new_version)
        else:
            version = version[:-(len(str(patch)) + 1)]
            minor = int(version.split('.')[-1])
            new_version = version[:-(len(str(minor)))] + str(minor+1)
        contents = re.sub(regex,
                          '    <version>' + new_version + '</version>',
                          contents,
                          1,
                          re.MULTILINE)
    with open('pom.xml', 'w') as pom:
        pom.write(contents)


replace_version()
