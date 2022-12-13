import re
import sys

is_dev = len(sys.argv) >= 2 and eval(sys.argv[1].lower().capitalize())
prep = not is_dev and len(sys.argv) >= 3 and bool(sys.argv[2])


def replace_version():
    regex = r'^[ ]{4}<version>((((\d+\.?)+)((-R(\d+)\.?)(\d+)?)?)(-SNAPSHOT)?)<\/version>$'
    with open('pom.xml', 'r') as pom:
        contents = pom.read()
        ver = re.findall(regex, contents, re.MULTILINE)
        version = ver[0][0]
        bare_version = ver[0][2]
        if is_dev:
            if not '-R' in version:
                new_version = version + '-R0.1-SNAPSHOT'
            elif not '-SNAPSHOT' in version:
                new_version = version + '.1-SNAPSHOT'
            else:
                r_version = ver[0][5]
                patch = int(ver[0][7]) + 1
                new_version = bare_version + r_version + str(patch) + '-SNAPSHOT'
        elif prep:
            r_version = int(ver[0][6]) + 1
            new_version = bare_version + '-R' + str(r_version)
        else:
            version = ver[0][2]
            minor = int(ver[0][3])
            new_version = version[:-(len(str(minor)))] + str(minor+1)
        contents = re.sub(regex,
                          '    <version>' + new_version + '</version>',
                          contents,
                          1,
                          re.MULTILINE)
    with open('pom.xml', 'w') as pom:
        pom.write(contents)


replace_version()
