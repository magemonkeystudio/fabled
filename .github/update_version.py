import re
import sys

is_dev = len(sys.argv) >= 2 and bool(sys.argv[1])


def replace_version():
    regex = r'^[ ]{4}<version>((\d+\.?)+)(-SNAPSHOT)?<\/version>$'
    with open('pom.xml', 'r') as pom:
        contents = pom.read()
        ver = re.findall(regex, contents, re.MULTILINE)
        version = ver[0][0]
        patch = int(ver[0][1])
        if is_dev:
            if len(version.split('.')) == 3:
                new_version = version + ".0-SNAPSHOT"
            else:
                new_version = version[:-(len(str(patch)))] + str(patch + 1) + '-SNAPSHOT'
        else:
            version = version[:-(len(str(patch)) + 1)]
            minor = int(version.split('.')[-1])
            new_version = version[:-(len(str(minor)))] + str(minor + 1)
        contents = re.sub(regex,
                          '    <version>' + new_version + '</version>',
                          contents,
                          1,
                          re.MULTILINE)
    with open('pom.xml', 'w') as pom:
        pom.write(contents)


replace_version()
