import sys


def write_lines_to_files(file, lines):
    with open(file, 'w') as file:
        file.writelines(lines)


def concat(files):
    print(files)

    # get header to append to result file later
    file1 = open(files[0], 'r') 
    header = file1.readlines()[0]
    file1.close()

    # open resulting file and add header to it
    f = open(files[-1], "a")
    f.write(header)

    concat_rec = []
    concat_rec.append(header)

    time_offset = 0.0
    print(files[:-1])
    for rec in files[:-1]:
        rec_file =  open(rec, 'r')
        lines = rec_file.readlines()[1:] #lines without taking the header
        offset_to_be = 0.0
        for line in lines:
            timestamp_as_str = line[1:].split(',')[0]
            new_timestamp = float(timestamp_as_str) + time_offset
            line = line.replace(timestamp_as_str, str(new_timestamp), 1)
            concat_rec.append(line)

            f.write(line)
            offset_to_be = new_timestamp
        time_offset = offset_to_be
    
    
        rec_file.close()
    f.close()

def squash(files, max_seconds):
    print(files)
    for f in files:
        rec_file =  open(f, 'r')
        lines = rec_file.readlines()
        rec_file.close()
        result = []
        result.append(lines[0]) #add header to result
        prev_timestamp = 0.0
        saved_seconds = 0.0
        for line in lines[1:]: #lines without taking the header
            curr_timestamp = float(line[1:].split(',')[0])
            
            if(curr_timestamp - prev_timestamp - max_seconds > 0):
                saved_seconds = saved_seconds + curr_timestamp - prev_timestamp - max_seconds
            new_timestamp = curr_timestamp - saved_seconds
            
            line = line.replace(str(curr_timestamp), str(new_timestamp), 1)
            result.append(line)
            
            prev_timestamp = curr_timestamp
        
        write_lines_to_files(f, result)
        print("By squashing we saved ", saved_seconds, " seconds")

def remove_exit_lines(files):
    for f in files:
        # readlines from file and close it
        rec_file = open(f, 'r')
        lines = rec_file.readlines()
        rec_file.close()

        # replace exit on the second last line by "" in 1 occurence
        lines[-1] = lines[-1].replace("exit", "", 1)
        print(lines[-1])
        write_lines_to_files(f, lines)

def print_doc():
    doc = """ 
    This is a small utility script for asciinema recordings.

        concat(files):
            If you want to remove the exit lines you will have to do it BEFORE this command!

            Takes arbitary number of files to concatenate in a single resulting file.
            The result will be stored in the last argument.
            
        squash(files):
            Will shorten the time of the recording and will change the files that were given as parameter.

        remove-exit-lines(files)
            Will NOT work on a already concatenated file.
            
            Will remove the last exit coming from the stopping of the asciinema recording in all the files given
            as parameters.
            """
    print(doc)

if __name__ == "__main__":
    files_in_args = sys.argv[2:]
    if len(sys.argv) == 1:
        print_doc()
    elif sys.argv[1] == "concat":
        concat(files_in_args)
    elif sys.argv[1] == "squash":
        squash(files_in_args, 1.0)
    elif sys.argv[1] == "remove-exit-line":
        remove_exit_lines(files_in_args)
    elif sys.argv[1] == "help":
        print_doc()
    else:
        print('This command is not supported, valid options are: concat, squash, '
              'remove-exit-line and help.')