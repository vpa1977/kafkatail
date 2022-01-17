using Buildalyzer;
using LibGit2Sharp;
using Microsoft.Build.Construction;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Security.Cryptography;
using System.Text;

namespace TheIndex
{
    class Program
    {

        private static IList<string> ContentProjectKeys = new[] { "None", "Compile", "Content", "EmbeddedResource" };
        private static IList<string> ReferenceProjectKeys = new[] { "ProjectReference" };

        private static IEnumerable<string> EnumerateProjectFiles(string projectPath, IList<string> includedKeys)
        {
            AnalyzerManager manager = new AnalyzerManager();
            IProjectAnalyzer analyzer = manager.GetProject(projectPath);
            IAnalyzerResults results = analyzer.Build();
            IAnalyzerResult result = results.Single();

            // If only interested in C# files, check out:
            //string[] sourceFiles = result.SourceFiles;

            var items = result.Items;
            foreach (var item in items)
            {
                // Skip keys like ProjectReference that aren't for files
                if (!includedKeys.Contains(item.Key))
                {
                    if (ReferenceProjectKeys.Contains(item.Key))
                    {
                        foreach (var project in item.Value)
                        {
                            var referencedFile = Path.Combine(Path.GetDirectoryName(projectPath), project.ItemSpec);
                            yield return referencedFile;
                            var referencedFiles = EnumerateProjectFiles(referencedFile, includedKeys);
                            foreach (var f in referencedFiles)
                                yield return f;
                        }
                        

                    }
                    continue;
                }
                    
                IProjectItem[] projectItems = item.Value;
                foreach (var projectItem in projectItems)
                {
                    // The item spec for files will be the path relative to the project directory
                    yield return projectItem.ItemSpec;
                }
            }
        }
        static void Main(string[] args)
        {
            var project = $"{args[0]}/{args[1]}";
            var dir = Path.GetDirectoryName(project);
            var files = EnumerateProjectFiles(project, ContentProjectKeys);
            Repository repo = new Repository(args[0]);
            
            var shaDict = repo.Index.ToDictionary(x => x.Path.Replace('/', Path.DirectorySeparatorChar).Replace('\\', Path.DirectorySeparatorChar), y => y.Id.Sha);
            var includedFiles = files.Select(x => Path.GetRelativePath(args[0], Path.Combine(dir, x)))
                .Select(x => shaDict[x]);
            var shaText = string.Join("",includedFiles.ToArray());
            using var shaAlgorithm = SHA256.Create();
            var hash = shaAlgorithm.ComputeHash(Encoding.UTF8.GetBytes(shaText));
            var finalHash =
                 string.Concat(Array.ConvertAll(hash,
                                      h => h.ToString("X2")));
            Console.WriteLine(finalHash);
        }
    }
}
